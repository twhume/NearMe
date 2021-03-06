package com.advsofteng.app1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.StaleDataException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class AddressBookRipperActivity extends Activity {

	private static final String TAG = "Ripper"; 
	private static final String KEY = "ASE-GROUP2";	/* Key used for SHA-1 encoding */
	private static final String ENDPOINT = "http://nearme.tomhume.org:8080/NearMeServer/";
	private GatherContactsTask gatherer = null;
	private AddressEntryAdapter adaptor = null;
	private SharedPreferences prefs = null;
	DialogBoxPermissions myDialog = null;

	private String countryCode = null;	/* ISO Country Code to be used for canonicalising MSISDNS */
	private String ownNumber = null; /* Users own phone number */
	private Button sendFriendList = null;
	
	private AddressBook myAddressBook = new AddressBook(); 
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);

		TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		countryCode = tm.getSimCountryIso();
		ownNumber = tm.getLine1Number();

		prefs = getApplicationContext().getSharedPreferences(NearMeActivity.TAG, Context.MODE_PRIVATE);

		gatherer = new GatherContactsTask();
		gatherer.execute();

		sendFriendList = (Button) this.findViewById(R.id.btnSendFriends);
		sendFriendList.setText(R.string.friends_loading);
		sendFriendList.setEnabled(false);

		sendFriendList.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
				UploadContactsTask uploader = new UploadContactsTask();
				uploader.execute( myAddressBook);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.addressbook, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.unsubscribe:
			UnsubscribeTask unsubscriber = new UnsubscribeTask();
			unsubscriber.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Turns a given MSISDN into a usable hash to identify the number. Does this by
	 * rendering the number into a canonical international MSISDN, then putting
	 * it through SHA-1
	 * 
	 * @param s input MSISDN
	 * @return
	 */
	private String hashMsisdn(String s) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		String input = null;
		PhoneNumber pn = null;

		try {
			pn = phoneUtil.parse(s, countryCode.toUpperCase());
			input = phoneUtil.format(pn,PhoneNumberFormat.E164);
			return sha1(input, KEY);
		} catch (Exception e) {
			// This is either a NumberFormatException or an error finding the crypto stuffs.
			// Either way ignore, we'll use the number as given if we can't format it
			return input;
		}
	}

	/**
	 * Hash function, taken from http://stackoverflow.com/questions/4534370/how-to-hash-string-using-sha-1-with-key
	 * 
	 * @param s
	 * @param keyString
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */

	private String sha1(String s, String keyString)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException {

		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"),
				"HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);
		byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
		return new String(Base64.encode(bytes, 0));
	}
	////////////////////////////////////////////

	private class UploadContactsTask extends AsyncTask<AddressBook, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(AddressBook... ab) {

			SharedPreferences.Editor editor = prefs.edit();
			boolean bContactsSaved = false;

			// loop through addressBook looking for perms != 0...
			for(int i = 0; i <  myAddressBook.getEntries().size(); i++){

				String name =  myAddressBook.getEntries().get(i).getName();
				int perms =  myAddressBook.getEntries().get(i).getPermission();

				if(AddressBookEntry.PERM_HIDDEN !=   myAddressBook.getEntries().get(i).getPermission()){

					//then save the current entry's permission in prefs, using their name as their unique ID for retrieval.
					editor.putInt(name, perms);
					bContactsSaved = true; // set flag

				}
				else{
					// we have a hidden perms... make sure its NOT in the prefs...
					editor.remove(name);
				}
			}
			editor.putBoolean("bContactsSaved", bContactsSaved); // set flag
			editor.commit(); // write to prefs once done checking

			//////////////////
			Gson gson = new Gson();
			Log.i(TAG, "Got entries " + ab[0].getEntries().size());

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ENDPOINT + "/addressBook");

			try {
				HttpEntity ent = new StringEntity(gson.toJson(ab[0]));
				post.setEntity(ent);
				HttpResponse response = client.execute(post);
				Log.i(TAG, "post to " + ENDPOINT + " done, response="+response.getStatusLine().getStatusCode());
			} catch (Exception e) {
				Log.i(TAG, "post threw " + e);
				e.printStackTrace();
				return false;
			}

			return true;
		}
	}

	/**
	 * Private class to implement listener to deal with permissions dialog box.
	 * Updates the AddressBookEntry with user-set permission, when dialog box is dismissed.
	 */

	private class OnReadyListener implements DialogBoxPermissions.ReadyListener {

		/**
		 * When the user clicks OK in the dialog box, save the fudge factor
		 * for the selected contact.
		 */
		
		@Override
		public void ready(String name) {
			int iPerms = myDialog.getFuzz();
			int iPosition = myDialog.getContactEntryNumber();
			  myAddressBook.getEntries().get(iPosition).setPermission(iPerms);
		}
	}

	/**
	 * Private class to gather contacts from the on-phone address book and use
	 * them to populate an AddressBook object in the background.
	 * 
	 * @author twhume
	 * 
	 */

	private class GatherContactsTask extends AsyncTask<Void, Integer, AddressBook> {

		protected void onPostExecute(AddressBook result) {
			Log.i(TAG, System.currentTimeMillis() + " done, result= " + result);

			  myAddressBook = result;

			//need to check that any local contacts have had their permissions saved previously in shared prefs.
			FindSavedPermsInPrefs();

			if (result!=null) {
				ListView list = (ListView)findViewById(R.id.friendslist);
				adaptor = new AddressEntryAdapter();
				list.setAdapter(adaptor);
				sendFriendList.setText(R.string.friends_loaded);
				sendFriendList.setEnabled(true);
			} else {
				Toast toast=Toast.makeText(getApplicationContext(), getString(R.string.ab_fail_grab), 2000);
				toast.show();
			}
		}

		/**
		 * Iterate through the address book, creating an AddressBook structure which
		 * contains a list of AddressBookEntries, which each contains a list of
		 * hashes
		 */

		@Override
		protected AddressBook doInBackground(Void... params) {
			Log.i(TAG, System.currentTimeMillis() + " starting");

			String[] columns = new String[] { Phone._ID, Phone.DISPLAY_NAME,
					Phone.NUMBER, Phone.RAW_CONTACT_ID };
			Uri contacts = Phone.CONTENT_URI;
			Cursor managedCursor = managedQuery(contacts, columns,
					null, // get all rows
					null, // Selection arguments (none)
					// Put the results in ascending order by name
					Phone.DISPLAY_NAME + " ASC");

			AddressBook a = new AddressBook();
			a.setDeviceId(prefs.getString(PreferencesActivity.KEY_ID, null));
			a.setOwnerHash(hashMsisdn(ownNumber));
			ArrayList<AddressBookEntry> entries = new ArrayList<AddressBookEntry>();
			ArrayList<String> hashes = new ArrayList<String>();
			String lastId = null;
			a.setEntries(entries);

			try {
				if (!managedCursor.moveToFirst()) return a;
				lastId = managedCursor.getString(3);
				AddressBookEntry abe = new AddressBookEntry();
				abe.setName(managedCursor.getString(1));

				do {

					if (!managedCursor.getString(3).equals(lastId)) {
						abe.setHashes(hashes);
						entries.add(abe);
						abe = new AddressBookEntry();
						abe.setName(managedCursor.getString(1));
						hashes = new ArrayList<String>();
					}
					hashes.add(hashMsisdn(managedCursor.getString(2))); 
					lastId = managedCursor.getString(3);

				} while (managedCursor.moveToNext());

				abe.setHashes(hashes);
				entries.add(abe);
			} catch (StaleDataException e) {
				e.printStackTrace();
				Log.w(TAG, "ripping of address book was interrupted by " + e);
				return null;
			} finally {
				managedCursor.close();
			}

			return a;
		}


		// finds any previously saved permissions data in shared prefs and retrieves it for our new AddressBook
		private void FindSavedPermsInPrefs(){

			//check that we have any contacts saved from last run...
			boolean bFlag = false;

			bFlag = prefs.getBoolean("bContactsSaved", bFlag);


			if(bFlag &&   myAddressBook!=null &&   myAddressBook.getEntries()!=null) {
				// we have at least 1 contact's permission saved so, change the addressbook entry to that permission.

				String strName = null;

				for(int i = 0; i<   myAddressBook.getEntries().size(); i++){

					strName =   myAddressBook.getEntries().get(i).getName();

					// check if current name has been saved before in prefs...
					//TODO consider the case when someone has a name which conflict with a key used to store datq in prefs
					if(prefs.contains(strName)){

						int iPerm = 0;

						iPerm = prefs.getInt(strName, iPerm);

						// if its any value other than 0, then replace this saved value in addressbook
						if(0 != iPerm){
							  myAddressBook.getEntries().get(i).setPermission(iPerm);
						}
					}
				}
			}

			return;

		}
	}

	/*
	 * Private Class that deals with our addressbook UI
	 * 
	 */
	class AddressEntryAdapter extends ArrayAdapter<AddressBookEntry> {
		AddressEntryAdapter() {
			super(AddressBookRipperActivity.this, 
					R.layout.row,  
					  myAddressBook.getEntries());
		}

		//ref: http://www.vogella.de/articles/AndroidListView/article.html
		public View getView( int position, View convertView, ViewGroup parent){

			
			View row=convertView;
			AddressHolder holder = null; // used to neatly fill row view of scroll list.
			final AddressBookEntry currentEntry = ((AddressBookEntry)  myAddressBook.getEntries().get(position));
			int iPermission = currentEntry.getPermission();
			final int iPosition = position;

			if (row==null) { // not drawn / created yet...
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.row, parent, false); // create view from row layout
				holder=new AddressHolder(row, iPermission); 

				
			// sets up callback function for current row's checkbox. Creates and shows new permissions dialog box.
				//	and sets the current entry's permission based on if box is checked or not. Subsequent changes in perms in dialog box is caught in 
				// dialog box's OnReadyListener callback function
				
				holder.friendCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							myDialog = new DialogBoxPermissions(AddressBookRipperActivity.this, new OnReadyListener(), iPosition);
							myDialog.show();
							currentEntry.setPermission(AddressBookEntry.PERM_SHOWN);
						}
						else{
							currentEntry.setPermission(AddressBookEntry.PERM_HIDDEN);
						}
					}

				});

				row.setTag(holder);

			}
			else{ // access existing row
				holder=(AddressHolder)row.getTag();
			}
			holder.populateFrom(  myAddressBook.getEntries().get(position));

			return(row);
		}

	}

	// Loads addressBookEntries details into UI arrayAdaptor
	static class AddressHolder {
		private TextView name=null;
		private CheckBox friendCheckBox = null;
		private boolean bCheckBox = false;

		AddressHolder(View row, int iPermission) {
			name=(TextView)row.findViewById(R.id.row_name);
			friendCheckBox = (CheckBox)row.findViewById(R.id.row_checkbox);

			// if we have an entry that is not hidden, then check the box here...
			if(AddressBookEntry.PERM_HIDDEN != iPermission)
				friendCheckBox.setChecked(true);

		}

		void populateFrom(AddressBookEntry entry) {

			if(AddressBookEntry.PERM_HIDDEN != entry.getPermission())
				bCheckBox = true;
			else
				bCheckBox = false;

			name.setText(entry.getName());
			friendCheckBox.setChecked(bCheckBox);

		}
	}

	/**
	 * Fires off an HTTP request to unsubscribe this app from the service
	 * 
	 * @author twhume
	 *
	 */

	private class UnsubscribeTask extends AsyncTask<Void, Integer, Void> {

		protected void onPostExecute(Void result) {
			finish();
			Toast toast=Toast.makeText(getApplicationContext(), getString(R.string.unsubscribed), 2000);
			toast.show();
		}

		/**
		 * Fire off the "unsubscribe me from this service" HTTP request
		 */

		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, System.currentTimeMillis() + " starting");

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ENDPOINT + "/unsubscribe/" + prefs.getString(PreferencesActivity.KEY_ID, ""));
			Log.d(NearMeActivity.TAG, "unsubscribing via " + post.getURI());
			try {
				HttpResponse response = client.execute(post);
				Log.i(TAG, "post to " + post.getURI() + " done, response="+response.getStatusLine().getStatusCode());
			} catch (Exception e) {
				Log.i(TAG, "post threw " + e);
				e.printStackTrace();
			}

			return null;
		}
	}



	protected void onStop() {

		if (  myAddressBook!=null &&   myAddressBook.getEntries()!=null) {
			SharedPreferences.Editor editor = prefs.edit();
			boolean bContactsSaved = false;
			// loop through addressBook looking for... 
			for(int i = 0; i <   myAddressBook.getEntries().size(); i++){
	
				int perms =   myAddressBook.getEntries().get(i).getPermission();
				String name =   myAddressBook.getEntries().get(i).getName();
	
				// ... non-hidden perms..
				if(AddressBookEntry.PERM_HIDDEN !=   myAddressBook.getEntries().get(i).getPermission()){
	
					//then save the current entry's permission in prefs, using their name as their unique ID for retrieval.
					editor.putInt(name, perms);
	
					bContactsSaved = true; // set flag - we have at least 1 shown contact...
	
				}
				else{ // we have a a hidden perms... make sure its NOT in the prefs...
					editor.remove(name);
	
				}
	
			}
			editor.putBoolean("bContactsSaved", bContactsSaved); // set flag
			editor.commit(); // write to prefs once done checking
		}
		super.onStop();

	}


}
