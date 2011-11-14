	package com.advsofteng.app1;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
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

	public static final String TAG = "LocationPoster";	/* used for logging purposes */
	public static final int MAGIC_NUMBER = 12345;		/* used as a reference to an Alarm */

	/* Interval between deliveries of location data to the server */
	private static final int POLL_INTERVAL = (5 * 60 * 1000);
	private LocationManager manager;
	
	private PendingIntent alarmIntent = null;	/* Handle to the repeatedly called Intent for triggering polls */
	private TextView tvGPS = null;				/* TextView to show GPS location on-screen */
	private DigitalClock clock = null;			/* on-screen clock */
	private SharedPreferences prefs = null;		/* used to share location & time between Activity and BroadcastReceiver */
	private Button button = null;				/* start/stop button */
	private Button buttonGetPOI = null;			/* get POIs button*/	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* Set the on-screen button to start and stop the location provider */
   
        button = (Button) findViewById(R.id.postButton);
        button.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View v) {

            	AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            	if (alarmIntent==null) {
            		
            		/* Start a regular process to poll the server */
            		
	            	Log.i(TAG,"starting poll");
	            	Calendar cal = Calendar.getInstance();
	        		Intent intent = new Intent(getApplicationContext(), LocationPoster.class);
	        		alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), MAGIC_NUMBER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), POLL_INTERVAL , alarmIntent);
	        		button.setText(getString(R.string.stop_button_label));
            	} else {
	            	Log.i(TAG,"finishing poll");

	            	/* Cancel the polling process */
	            	
            		am.cancel(alarmIntent);
            		alarmIntent = null;
            		button.setText(getString(R.string.start_button_label));
            	}
            }
        });
        
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
		
		
     
        //////////////////////////////////////////
        // deals with getPOI button.
        buttonGetPOI = (Button) findViewById(R.id.getPOIButton);
        buttonGetPOI.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				Intent intentPOI = new Intent(AdvSoftEngApp1Activity.this, GetPOIActivity.class);
				
				startActivity(intentPOI);
				
			} // end of onClickView(View v)
			}//  end of View.OnClickListener
        ); // end of setOnClickListener
        // 
        /////////////////////////////
           
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

		Log.i(TAG, "set time to "+ clock.getText());
		
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("time", new Date().toString());
		edit.putString("latitude", Double.toString(location.getLatitude()));
		edit.putString("longitude", Double.toString(location.getLongitude()));
		edit.commit();
		Log.i(TAG, "saved location OK");
		Log.i(TAG, "new time="+prefs.getString("time", "notset"));
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
	
	
}


