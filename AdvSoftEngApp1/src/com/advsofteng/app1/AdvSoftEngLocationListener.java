package com.advsofteng.app1;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class AdvSoftEngLocationListener implements LocationListener {

	private AdvSoftEngApp1Activity locationActivity = null;
			
	public AdvSoftEngLocationListener(AdvSoftEngApp1Activity locationActivity) {
		this.locationActivity = locationActivity;
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(AdvSoftEngApp1Activity.TAG,"onStatusChanged");
		if((LocationProvider.OUT_OF_SERVICE == status)||(LocationProvider.TEMPORARILY_UNAVAILABLE == status)) 
		{	
			Log.i(AdvSoftEngApp1Activity.TAG,"onStatusChanged out of service");
			locationActivity.setLocation(null);
		}
	}

	/**
	 * When we receive a location, pass it to the activity for display and saving
	 */
	
	public void onLocationChanged(Location location) {
		locationActivity.setLocation(location);		
	}

	public void onProviderEnabled(String provider) {
		// TODO put code here to deal with GPS being down...
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}




}
