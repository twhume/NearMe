package com.advsofteng.app1;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class NearMeLocationListener implements LocationListener {

	private NearMeActivity locationActivity = null;
			
	public NearMeLocationListener(NearMeActivity locationActivity) {
		this.locationActivity = locationActivity;
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(NearMeActivity.TAG,"onStatusChanged");
		if((LocationProvider.OUT_OF_SERVICE == status)||(LocationProvider.TEMPORARILY_UNAVAILABLE == status)) 
		{	
			Log.i(NearMeActivity.TAG,"onStatusChanged out of service");
			locationActivity.setLocation(null);
		}
	}

	/**
	 * When we receive a location, pass it to the activity for display and saving
	 */
	
	public void onLocationChanged(Location location) {
		locationActivity.setLocation(location);		
	}

	public void onProviderEnabled(String provider) {}
	
	public void onProviderDisabled(String provider) {
		/* Turn off the display of location information */
		locationActivity.setLocation(null);
	}




}
