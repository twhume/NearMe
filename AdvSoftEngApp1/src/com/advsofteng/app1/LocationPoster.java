package com.advsofteng.app1;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Class which actions delivery of a location and timestamp to a server using an
 * HTTP POST
 * 
 * @author twhume
 * 
 */
public class LocationPoster extends BroadcastReceiver {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://192.168.1.77:8080/location";

	/* Fields which must be present in order for a POST to be triggered */
	private static final String[] REQUIRED_FIELDS = { "id", "time", "latitude",
			"longitude" };

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(AdvSoftEngApp1Activity.TAG, "poll triggered");


		/* Create a new HTTPClient to do our POST for us */

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(ENDPOINT);
		AdvSoftEngLocationListener listener = AdvSoftEngApp1Activity.listener;

		Log.i(AdvSoftEngApp1Activity.TAG, "loc="+listener.getLastLocation());
		if (listener.getLastLocation() != null) {
			Log.i(AdvSoftEngApp1Activity.TAG, "got last location");
			try {

				/* Set the payload for this POST */

				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("id", "1"));
				params.add(new BasicNameValuePair("longitude", Double
						.toString(listener.getLastLocation().getLongitude())));
				params.add(new BasicNameValuePair("latitude", Double
						.toString(listener.getLastLocation().getLatitude())));
				params.add(new BasicNameValuePair("time", listener
						.getLastTime()));

				// FIXME race condition above, where the listener receives a new
				// update in the middle of this block of code

				post.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse response = client.execute(post);
				Log.i(AdvSoftEngApp1Activity.TAG, "post done, response="+response.getStatusLine().getStatusCode());

				Toast.makeText(context, "Poll OK", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.i(AdvSoftEngApp1Activity.TAG, "post failed, " + e.getMessage());
				Toast.makeText(context, "Poll failed: " + e.getMessage(),
						Toast.LENGTH_LONG).show();

				/* In the event of a failure, cancel any subsequent polls */

				AlarmManager am = (AlarmManager) context
						.getSystemService(Activity.ALARM_SERVICE);
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
						12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				am.cancel(alarmIntent);
			}
		}
		Log.i(AdvSoftEngApp1Activity.TAG, "poll over");

	}
}
