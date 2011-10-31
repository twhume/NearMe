package com.advsofteng.app1;

import java.util.Calendar;

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

	/* Interval between deliveries of location data to the server */
	private static final int POLL_INTERVAL = (5 * 1000);
	private LocationManager manager;
	public static AdvSoftEngLocationListener listener;
	
	private PendingIntent alarmIntent = null;	/* Handle to the repeatedly called Intent for triggering polls */
	private TextView tvGPS = null;				/* TextView to show GPS location on-screen */
	private DigitalClock clock = null;			/* on-screen clock */
	private SharedPreferences prefs = null;		/* used to share location & time between Activity and BroadcastReceiver */
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /* Set the on-screen button to start and stop the location provider */
        
        final Button button = (Button) findViewById(R.id.postButton);
        button.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View v) {

            	AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            	if (alarmIntent==null) {
            		
            		/* Start a regular process to poll the server */
            		
	            	Log.i(TAG,"starting poll");
	            	Calendar cal = Calendar.getInstance();
	        		cal.add(Calendar.SECOND,0);
	        		Intent intent = new Intent(getApplicationContext(), LocationPoster.class);
	        		alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        setGPSText(loc);
      
		/* connect the listener object to receive GPS updates */

        listener = new AdvSoftEngLocationListener();
        listener.setLocationActivity(this);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
           
    }
    
	/**
	 * updates TextView tv text with location's longitude and latitude data.
	 * 
	 * @param location
	 * @param tv
	 */
    
    public void setGPSText(Location location)
    {
    	/* If we get a null location, clear out the preferences
    	 * and pop up the error message
    	 */
    	
    	if (null == location) {
    		emptyPrefs();
    		tvGPS.setText(getString(R.string.no_gps_error));
    		return;
    	}
    	
		String strGPS = "Longitude = " + location.getLongitude() + "\n";
		strGPS += "Latitude = " + location.getLatitude();
		tvGPS.setText(strGPS);
		
		// then slap it into those shared preferences so it gets sent up in a poll

		prefs.edit().putString("time", clock.getText().toString());
		prefs.edit().putString("latitude", Double.toString(location.getLatitude()));
		prefs.edit().putString("longitude", Double.toString(location.getLongitude()));
    }
    
    private void emptyPrefs() {
		prefs.edit().remove("time");
		prefs.edit().remove("latitude");
		prefs.edit().remove("longitude");
    }

}


