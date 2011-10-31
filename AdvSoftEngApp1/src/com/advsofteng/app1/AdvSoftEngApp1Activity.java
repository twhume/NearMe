package com.advsofteng.app1;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
	private LocationListener listener;
	private String strGPS;
	private PendingIntent alarmIntent = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        /* Set the on-screen button to start and stop the location provider */
        
        final Button button = (Button) findViewById(R.id.postButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            	if (alarmIntent==null) {
	            	Log.i(TAG,"starting poll");

	            	Calendar cal = Calendar.getInstance();
	        		cal.add(Calendar.SECOND,1);
	        		Intent intent = new Intent(getApplicationContext(), LocationPoster.class);
	        		intent.putExtra("alarm_message", "O'Doyle Rules!");
	        		alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), POLL_INTERVAL , alarmIntent);

	        		button.setText("Stop");
            	} else {
	            	Log.i(TAG,"finishing poll");
	            	
            		am.cancel(alarmIntent);
            		alarmIntent = null;

            		button.setText("Start");
            	}
            }
        });
        
        // get handle to the GPS TextView
        final TextView tvGPS = (TextView) findViewById(R.id.textViewGPS);
        
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        // We may know a location even before we start; if so, use it.
        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if(loc != null) setGPSText(loc, tvGPS);
        else tvGPS.setText(getString(R.string.no_gps_error));
      
		// create the listener object to receive GPS updates...
		listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if((LocationProvider.OUT_OF_SERVICE == status)||(LocationProvider.TEMPORARILY_UNAVAILABLE == status)) 
				{	// if there's no service, print error string found in resources...
					tvGPS.setText(getString(R.string.no_gps_error));
				}
			}

			@Override
			public void onLocationChanged(Location location) {
				// update GPS TextView's data
				//TODO - TAKE THIS OUT...
				setGPSText(location, tvGPS);
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO put code here to deal with GPS being down...
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}
			
		};
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
           
    }
    
	/**
	 * updates TextView tv text with location's longitude and latitude data.
	 * 
	 * @param location
	 * @param tv
	 */
    
    private void setGPSText(Location location, TextView tv)
    {
    	// check for valid TextView and Location objects....
    	if((null == tv) || (null == location)) return;
    	
		strGPS = "Longitude = " + location.getLongitude() + "\n";
		strGPS += "Latitude = " + location.getLatitude();
		tv.setText(strGPS);
    }

	public void showConnectionFailure() {
		
		CharSequence text = "Report failed"; //TODO externalise
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
		// TODO reset button label to basic
	}

	
}


