package com.advsofteng.app1;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Class which actions delivery of a location and timestamp to a server
 * 
 * @author twhume
 *
 */
public class LocationPoster extends BroadcastReceiver {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://localhost:8080/";
	
	/* Fields which must be present in order for a POST to be triggered */
	private static final String[] REQUIRED_FIELDS = {"id","time","latitude","longitude"};

	private HttpClient client = new DefaultHttpClient();
	private List<NameValuePair> payload = null;
		
	public void set(String name, String value) {
		payload.add(new BasicNameValuePair(name, value));
	}
	
	public boolean post() {
	    Log.i(AdvSoftEngApp1Activity.TAG,"post");
	    HttpPost post = new HttpPost(ENDPOINT);
	    if (2==2) return true;

	    try {
	        post.setEntity(new UrlEncodedFormEntity(payload));
	        HttpResponse response = client.execute(post);
			payload.clear(); /* clear out data after a poll - we just sent it */
			//FIXME Thread-unsafe!
	    } catch (ClientProtocolException e) {
	    	/* In the event of any problems, stop polling */
	    	return false;
	    } catch (IOException e) {
	    	/* In the event of any problems, stop polling */
	    	return false;
	    }
	    return true;
	}
	
	private boolean validPayload() {
		if (payload==null) return false;
		if (payload.size()!=REQUIRED_FIELDS.length) return false;

		//TODO: add routine to check valid names in payload
		
		return true;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i(AdvSoftEngApp1Activity.TAG,"alarmed!");
		if (validPayload()) post();		
		Toast.makeText(context, "Doin' mah pollin'", Toast.LENGTH_LONG).show();
	}
	
}
