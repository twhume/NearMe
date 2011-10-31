package com.advsofteng.app1;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Class which actions delivery of a location and timestamp to a server
 * using an HTTP POST
 * 
 * @author twhume
 *
 */
public class LocationPoster extends BroadcastReceiver {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://localhost:8080/";
	
	/* Fields which must be present in order for a POST to be triggered */
	private static final String[] REQUIRED_FIELDS = {"id","time","latitude","longitude"};

	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i(AdvSoftEngApp1Activity.TAG,"alarmed!");
		
	    try {
			HttpClient client = new DefaultHttpClient();
		    HttpPost post = new HttpPost(ENDPOINT);
	    	/* Get the Payload */
//		    post.setEntity(new UrlEncodedFormEntity(payload));
	        HttpResponse response = client.execute(post);

	        Toast.makeText(context, "Poll OK", Toast.LENGTH_SHORT).show();
	    } catch (Exception e) {
			Toast.makeText(context, "Poll failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
			
			/* In the event of a failure, cancel any subsequent polls */
			
			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
    		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.cancel(alarmIntent);
		}
	}
	
}
