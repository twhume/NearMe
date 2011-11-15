package org.tomhume.ase.ripper;

import java.util.ArrayList;

import com.google.gson.Gson;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddressBookRipperActivity extends Activity {

	private static final String TAG = "Ripper";
	private GatherContactsTask gatherer = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button ripButton = (Button) this.findViewById(R.id.btnRip);
		gatherer = new GatherContactsTask();

		ripButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "CLICK!");

				if (gatherer.getStatus().equals(Status.RUNNING))
					return;
				if (gatherer.getStatus().equals(Status.FINISHED))
					gatherer = new GatherContactsTask();
				gatherer.execute();

			}
		});
	}

	private void uploadContacts(AddressBook a) {
    	UploadContactsTask uploader = new UploadContactsTask();
    	uploader.execute(a);
    }

	private class UploadContactsTask extends AsyncTask<AddressBook, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(AddressBook... abe) {
			Gson gson = new Gson();
			Log.i(TAG, "Got entries " + abe[0].getEntries().size());
			System.err.println(gson.toJson(abe[0]));

			return true;
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

		protected void onProgressUpdate(Integer... progress) {
			Log.i(TAG, System.currentTimeMillis() + " progress, current= "
					+ progress[0].intValue());
			// TODO implement a callback into a progress bar, if this task takes  any time on slow phones
		}

		/**
		 * Once we're done, trigger the uploading process
		 */

		protected void onPostExecute(AddressBook result) {
			Log.i(TAG, System.currentTimeMillis() + " done, result= " + result);
			uploadContacts(result);
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
			a.setDeviceId(Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));
			ArrayList<AddressBookEntry> entries = new ArrayList<AddressBookEntry>();
			ArrayList<String> hashes = new ArrayList<String>();
			String lastId = null;

			try {
				managedCursor.moveToFirst();
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
					//TODO actually hash this so that we're not delivering numbers to the server
					hashes.add(managedCursor.getString(2)); 
					lastId = managedCursor.getString(3);

				} while (managedCursor.moveToNext());
				
				abe.setHashes(hashes);
				entries.add(abe);

			} finally {
				managedCursor.close();
			}

			a.setEntries(entries);
			return a;
		}
	}

}