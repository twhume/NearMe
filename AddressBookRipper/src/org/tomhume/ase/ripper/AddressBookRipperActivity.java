package org.tomhume.ase.ripper;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddressBookRipperActivity extends Activity {
	
	private static final String TAG = "Ripper";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button ripButton = (Button)this.findViewById(R.id.btnRip);
        ripButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Log.i(TAG, "CLICK!");
        	  
        	  
        	  String[] projection = new String[] {
        			  Phone._ID,
                      Phone.DISPLAY_NAME,
                      Phone.NUMBER
                   };
        	  Uri contacts =  Phone.CONTENT_URI;
        	  Cursor managedCursor = managedQuery(contacts,
                      projection, // Which columns to return 
                      null,       // Which rows to return (all rows)
                      null,       // Selection arguments (none)
                      // Put the results in ascending order by name
                      Phone.LABEL + " ASC");
        	  try {
        		  managedCursor.moveToFirst();
        		  do {
            		  Log.i(TAG, "Name="+ managedCursor.getString(1) + "=>" + managedCursor.getString(2));
        		  } while (managedCursor.moveToNext());
        	  } finally {
        		  managedCursor.close();
        	  }
          }
        });
    }
}