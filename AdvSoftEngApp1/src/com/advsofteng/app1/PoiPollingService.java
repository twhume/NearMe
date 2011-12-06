package com.advsofteng.app1;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * A class to poll in the  background at regular intervals, looking for points of interest
 * @author twhume
 *
 */

public class PoiPollingService extends BroadcastReceiver {

	/* Interval between deliveries of location data to the server */
	private static final int POLL_INTERVAL = (5 * 60 * 1000);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(AdvSoftEngApp1Activity.TAG, "poll triggered");

		/* Last-reported locations live in SharedPreferences, so they can be shared between
		 * Activities and BroadcastReceivers. If there's nothing there, don't do a post and
		 * tell the user we couldn't report in
		 */
		
		SharedPreferences prefs = context.getSharedPreferences(AdvSoftEngApp1Activity.TAG, Context.MODE_PRIVATE);
		if (prefs.getString("time", null)==null) {
			Log.i(AdvSoftEngApp1Activity.TAG, "no GPS yet, don't report");
			Toast toast=Toast.makeText(context, context.getString(R.string.no_gps_error), 2000);
			toast.setGravity(Gravity.TOP, -30, 50);
			toast.show();
			return;
		}

		int radius = prefs.getInt("radius", 0);
		double lng = prefs.getInt("longitude", 0);
		double lat = prefs.getInt("latitude", 0);
		String types = prefs.getString("types", null);
		
		try {

   			String myUrl = AdvSoftEngApp1Activity.ENDPOINT
   					+ "/nearme/"
   					+ AdvSoftEngApp1Activity.DEVICE_ID
   					+ "/"
   					+ lat
   					+ "/"
   					+ lng
   					+ "/"
   					+ radius;
			
   			/* types are an optional parameter */
   			
   			if (types!=null) {
				myUrl = myUrl + "?t=" + types;
   			}
				
			Log.d(AdvSoftEngApp1Activity.TAG, "get="+myUrl);

			/* Create a new HTTPClient to do our GET for us */

			HttpGet get = new HttpGet(myUrl);   					
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			String responseBody = HttpHelper.getResponseBody(response);
				

			/* get the Poi objects out of the string returned
			 * ref: http://benjii.me/2010/04/deserializing-json-in-android-using-gson/
			 */
				
			Gson gson = new Gson();
				
			ArrayList<Poi> newPois = new ArrayList<Poi>();
			boolean gotNewPoi = false;

			Log.d(AdvSoftEngApp1Activity.TAG, "received json="+responseBody);
		    JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(responseBody).getAsJsonArray();
		    
		    for(JsonElement counter : array)
		    {	
		    	// run through the JsonArray converting each entry into an actual Poi object in the Poi ArrayList
		    	Poi p = gson.fromJson(counter, Poi.class);
		    	if (!AdvSoftEngApp1Activity.poiArray.contains(p)) gotNewPoi = true;
		    	newPois.add(p);
		    }
		    
		    AdvSoftEngApp1Activity.poiArray = newPois;
		    
		    /*
		     * If we received any new Pois as part of this update, then alert the user
		     */
		    
		    if (gotNewPoi) {
			  Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			  vib.vibrate(300);
			  //TODO put in an Android notification here?
		    }
			
		} catch (Exception e) {
			Log.i( AdvSoftEngApp1Activity.TAG, "get to getPOI failed, " + e.getMessage());
			
		}

		Log.i(AdvSoftEngApp1Activity.TAG, "poll over");
	}
}
