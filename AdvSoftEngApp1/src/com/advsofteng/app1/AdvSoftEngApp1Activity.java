package com.advsofteng.app1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextView;

/**
 * @author alandonohoe
 * 
 * This App displays the current time and the device's current GPS co-ordinates.
 * if GPS data is unavailable, it displays "GPS not available".
 * 
 */
public class AdvSoftEngApp1Activity extends Activity {

	public static final String ENDPOINT = "http://nearme.tomhume.org:8080/NearMeServer";
	public static final String TAG = "LocationPoster";	/* used for logging purposes */
	public static final int MAGIC_NUMBER = 12345;		/* used as a reference to an Alarm */
	public static String DEVICE_ID = null;
	
	/* Interval between deliveries of location data to the server */
	private static final int POLL_INTERVAL = (5 * 60 * 1000);
	private LocationManager manager;
	
	private PendingIntent alarmIntent = null;	/* Handle to the repeatedly called Intent for triggering polls */
	private TextView tvGPS = null;				/* TextView to show GPS location on-screen */
	private DigitalClock clock = null;			/* on-screen clock */
	private SharedPreferences prefs = null;		/* used to share location & time between Activity and BroadcastReceiver */
	private Button button = null;				/* start/stop button */
	private Button buttonGetPOI = null;			/* get POIs button*/
	private Button buttonMap = null;         /* View Map POI Button*/
	private Button buttonAddressBookRip = null;
	private Button buttonAdd = null;
	private Button buttonUnsubscribe = null;	/* "unsubscribe me" button */
	
	public static ArrayList<Poi>  poiArray = new ArrayList<Poi>(); 
	public static AddressBook globalAddressBook = new AddressBook();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.add_poi:
		        addPlace();
		        return true;
		    case R.id.upload_ab:
		    	manageAddressBook();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void addPlace() {
		Intent intentAdd = new Intent(AdvSoftEngApp1Activity.this, addPlace.class);
		startActivity(intentAdd);
	}
	
	private void manageAddressBook() {
		Intent intentAddressRip = new Intent(AdvSoftEngApp1Activity.this, AddressBookRipperActivity.class);
		startActivity(intentAddressRip);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEVICE_ID==null) DEVICE_ID = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        
        setContentView(R.layout.main);

        
        /* get handles to the TextView where GPS position is displayed, and the on-screen clock */
        
        tvGPS = (TextView) findViewById(R.id.textViewGPS);
        clock = (DigitalClock) findViewById(R.id.digitalClock1);
        
        /* We may know a location even before we start; if so, use it. */

        prefs = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setLocation(loc);

      
		/* connect the listener object to receive GPS updates */

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new AdvSoftEngLocationListener(this));
		
		
	   //**********************************
		// Deal with add favourite button
		buttonAdd=  (Button) findViewById(R.id.addButton);
	    buttonAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addPlace();
			}
	       });
	        
		
		
	   //*************************************
        //Deal with view map button
       
       buttonMap=  (Button) findViewById(R.id.mapButton);
       buttonMap.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			Intent intentMAP = new Intent(AdvSoftEngApp1Activity.this, map.class);
			
			startActivity(intentMAP);
			
		}
       });
        
 
        //////////////////////////////////////////
        // deals with getPOI button.
        buttonGetPOI = (Button) findViewById(R.id.getPOIButton);
        buttonGetPOI.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				
				Intent intentPOI = new Intent(AdvSoftEngApp1Activity.this, GetPOIActivity.class);
				
				startActivity(intentPOI);
				
				 buttonMap.setVisibility(0);
				
			} // end of onClickView(View v)
			}//  end of View.OnClickListener
        ); // end of setOnClickListener
        // 
        /////////////////////////////
        
        //////////////////////////////
        //
        
        buttonAddressBookRip = (Button) findViewById(R.id.launchAddressBookRipper);
        buttonAddressBookRip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manageAddressBook();
			}
		});
        
        buttonUnsubscribe = (Button) findViewById(R.id.unsubButton);
        buttonUnsubscribe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	UnsubscribeTask unsubscriber = new UnsubscribeTask();
		    	unsubscriber.execute();
			}
		});
        
        
     
           
    }
    
	/**
	 * updates TextView tv text with location's longitude and latitude data.
	 * 
	 * @param location
	 * @param tv
	 */
    
    public void setLocation(Location location)
    {
    	/* If we get a null location, clear out the preferences
    	 * and pop up the error message
    	 */
    	
    	if (null == location) {
    		Log.i(TAG, "null location, clearing out");
    		emptyPrefs();
    		tvGPS.setText(getString(R.string.no_gps_error));
    		return;
    	}
    	
		String strGPS = "Longitude = " + location.getLongitude() + "\n";
		strGPS += "Latitude = " + location.getLatitude();
		tvGPS.setText(strGPS);
		
		// then slap it into those shared preferences so it gets sent up in a poll

		//TODO: temp commented this out, as it was sending too much to the log - AD.
		//Log.i(TAG, "set time to "+ clock.getText());
		
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("time", new Date().toString());
		edit.putString("latitude", Double.toString(location.getLatitude()));
		edit.putString("longitude", Double.toString(location.getLongitude()));
		edit.commit();
		//TODO: temp commented out this line - it was sending too much data to the log - AD
		//Log.i(TAG, "saved location OK");
		//Log.i(TAG, "new time="+prefs.getString("time", "notset"));
    }
    
    private void emptyPrefs() {
		Log.i(TAG, "emptyPrefs");
		SharedPreferences.Editor edit = prefs.edit();
		edit.remove("time");
		edit.remove("latitude");
		edit.remove("longitude");
		edit.commit();
		
    }

	protected void onPause() {
		super.onPause();

		/* Leaving the app, even briefly? Cancel any subsequent polls and reset button to start */

		if (alarmIntent!=null) {
			Context ctx = getApplicationContext();
			AlarmManager am = (AlarmManager) ctx.getSystemService(Activity.ALARM_SERVICE);
			am.cancel(alarmIntent);
			alarmIntent = null;
			button.setText(R.string.start_button_label);
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
			Log.i(TAG, System.currentTimeMillis() + " done, result= " + result);
			buttonUnsubscribe.setEnabled(false);
		}

		/**
		 * Fire off the "unsubscribe me from this service" HTTP request
		 */
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, System.currentTimeMillis() + " starting");
			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ENDPOINT + "/unsubscribe/" + DEVICE_ID);

			try {
				HttpResponse response = client.execute(post);
				Log.i(TAG, "post to " + post.getURI() + " done, response="+response.getStatusLine().getStatusCode());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "post threw " + e);
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
}


