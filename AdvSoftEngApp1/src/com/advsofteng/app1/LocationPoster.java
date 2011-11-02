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
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
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
	private static final String ENDPOINT = "http://107.22.213.41:8080/Task3Server/ServletTask3";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(AdvSoftEngApp1Activity.TAG, "poll triggered");

		/* Last-reported locations live in SharedPreferences, so they can be shared between
		 * Activities and BroadcastReceivers. If there's nothing there, don't do a post.
		 */
		
		SharedPreferences prefs = context.getSharedPreferences(AdvSoftEngApp1Activity.TAG, Context.MODE_PRIVATE);
		if (prefs.getString("time", null)==null) {
			Log.i(AdvSoftEngApp1Activity.TAG, "nothing to report");
			return;
		}

		/* Create a new HTTPClient to do our POST for us */

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(ENDPOINT);

		Log.i(AdvSoftEngApp1Activity.TAG, "time="+prefs.getString("time", null));

		try {

			/* Set the payload for this POST */

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", Secure.getString(context.getContentResolver(),Secure.ANDROID_ID)));
			params.add(new BasicNameValuePair("longitude", prefs.getString("longitude", "")));
			params.add(new BasicNameValuePair("latitude", prefs.getString("latitude", "")));
			params.add(new BasicNameValuePair("time", prefs.getString("time", "")));

			// FIXME race condition above, where the SharedPreferences are updated in the middle of this block of code

			post.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse response = client.execute(post);
			Log.i(AdvSoftEngApp1Activity.TAG, "post done, response="+response.getStatusLine().getStatusCode());

			Toast.makeText(context, "Poll OK", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.i(AdvSoftEngApp1Activity.TAG, "post failed, " + e.getMessage());
			Toast.makeText(context, "Poll failed: " + e.getMessage(), Toast.LENGTH_LONG).show();

			/* In the event of a failure, cancel any subsequent polls */

			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.cancel(alarmIntent);
		}

		Log.i(AdvSoftEngApp1Activity.TAG, "poll over");
	}
}