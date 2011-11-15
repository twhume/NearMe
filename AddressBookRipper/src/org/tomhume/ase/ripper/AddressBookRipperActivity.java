package org.tomhume.ase.ripper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddressBookRipperActivity extends Activity {
	
	private static final String TAG = "Ripper";
	private UploadContactsTask uploader = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button ripButton = (Button)this.findViewById(R.id.btnRip);
        uploader = new UploadContactsTask();
        
        ripButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Log.i(TAG, "CLICK!");
        	  
        	  if (uploader.getStatus().equals(Status.RUNNING)) return;
        	  if (uploader.getStatus().equals(Status.FINISHED)) uploader = new UploadContactsTask();
        	  uploader.execute();

          }
        });
    }
    
    private class UploadContactsTask extends AsyncTask<Void, Integer, Boolean> {

        protected void onProgressUpdate(Integer... progress) {
        	Log.i(TAG, System.currentTimeMillis() + " progress, current= "+ progress[0].intValue());
        }

        protected void onPostExecute(Boolean result) {
        	Log.i(TAG, System.currentTimeMillis() +  " done, result= "+ result);
        }

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.i(TAG, System.currentTimeMillis() + " starting");

      	  String[] projection = new String[] {
    			  Phone._ID,
                  Phone.DISPLAY_NAME,
                  Phone.NUMBER,
                  Phone.RAW_CONTACT_ID
               };
    	  Uri contacts =  Phone.CONTENT_URI;
    	  Cursor managedCursor = managedQuery(contacts,
                  projection, // Which columns to return 
                  null,       // Which rows to return (all rows)
                  null,       // Selection arguments (none)
                  // Put the results in ascending order by name
                  Phone.DISPLAY_NAME + " ASC");
    	  try {
    		  managedCursor.moveToFirst();
    		  do {
        		  Log.i(TAG, managedCursor.getString(3) + ": "+ managedCursor.getString(1) + "=>" + managedCursor.getString(2));
    		  } while (managedCursor.moveToNext());
    	  } finally {
    		  managedCursor.close();
    	  }
			
			return true;
		}
    }
    
}