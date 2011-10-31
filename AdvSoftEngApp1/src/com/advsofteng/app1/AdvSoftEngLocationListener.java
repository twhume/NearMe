package com.advsofteng.app1;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class AdvSoftEngLocationListener implements LocationListener {

	private AdvSoftEngApp1Activity locationActivity = null;
	
	/* Keep track of the last Location we received, and the last date and time it was received at */
	private Location lastLocation = null;
	private String lastTime = null;
	
	/* It's a singleton. Sorry - this isn't considered great practice nowadays, I should
	 * spend more time looking into ways of sharing active objects between bits of an 
	 * Android app. TODO
	 */
		
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(AdvSoftEngApp1Activity.TAG,"onStatusChanged");

		if((LocationProvider.OUT_OF_SERVICE == status)||(LocationProvider.TEMPORARILY_UNAVAILABLE == status)) 
		{	// if there's no service, print error string found in resources...
			locationActivity.setGPSText(R.string.no_gps_error);
			lastLocation = null;
			lastTime = null;
		}
	}

	/**
	 * When we receive a location, put it on-screen and save it as the last-known-good
	 */
	
	public void onLocationChanged(Location location) {
		Log.i(AdvSoftEngApp1Activity.TAG,"onLocationChanged");
		locationActivity.setGPSText(location);
		lastLocation = location;
		lastTime = locationActivity.getTime();
	}

	public void onProviderEnabled(String provider) {
		// TODO put code here to deal with GPS being down...
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	/**
	 * Set the Activity which should be updated when changes to GPS state occur
	 * 
	 * @param locationActivity
	 */

	public void setLocationActivity(AdvSoftEngApp1Activity locationActivity) {
		this.locationActivity = locationActivity;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public String getLastTime() {
		return lastTime;
	}


}
