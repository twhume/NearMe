package com.advsofteng.app1;

import android.app.Activity;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author alandonohoe
 * This App displays the current time and the device's current GPS co-ordinates.
 * if GPS data is unavailable, it displays "GPS not available".
 */
public class AdvSoftEngApp1Activity extends Activity implements FailureHandler {
	
	// class members
	private LocationManager manager;
	private LocationListener listener;
	private LocationPoster poster;
	private String strGPS;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        poster = new LocationPoster(this);
        setContentView(R.layout.main);
        
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

	public void fail() {
		CharSequence text = "Report failed"; //TODO externalise
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
		// TODO reset button label to basic
	}
    
    
}


