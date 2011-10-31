package com.advsofteng.app1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.util.Log;

/**
 * Class which actions delivery of a location and timestamp to a server
 * 
 * @author twhume
 *
 */
public class LocationPoster implements Runnable {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://localhost:8080/";
	
	/* How long to wait between sending locations to the server */
	private static final int DELAY = (5 /* * 60 */ * 1000); // every 5 minutes
	
	/* Fields which must be present in order for a POST to be triggered */
	private static final String[] REQUIRED_FIELDS = {"id","time","latitude","longitude"};

	private static final String TAG = "LocationPoster";
	
	private Handler failCallback = null;
	private Runnable failThread = null;
	private Thread theThread = null;
	private HttpClient client = null;
	private List<NameValuePair> payload = null;
	private boolean finishSignalled = false;
	
	public LocationPoster(Handler f, Runnable r) {
		Log.i(TAG, "new");
		failCallback = f;
		failThread = r;
		
		client = new DefaultHttpClient();
		reset();
	}
	
	private void reset() {
		Log.i(TAG, "reset");
		payload = new ArrayList<NameValuePair>(); 
		finishSignalled = false;
		theThread = null;
	}
	
	public void stop() {
		this.finishSignalled = true;
	}
	
	public void start() {
		Log.i(TAG, "start");
		if (!isRunning()) {
			theThread = new Thread(this);
			theThread.start();
		} else Log.i(TAG, "not spawning thread");
	}	

	public boolean isRunning() {
		Log.i(TAG, "isRunning="+ ((theThread!=null) && (!finishSignalled)));
		return ((theThread!=null) && (!finishSignalled));
	}
	
	public void set(String name, String value) {
		payload.add(new BasicNameValuePair(name, value));
	}
	
	public boolean post() {
	    Log.i(TAG,"post");
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
	
	public void run() {
		Log.i(TAG, "run");
		while (!finishSignalled) {
			/* Post up data to the server, but only if we have a time, latitude and longitude */
			/*if (validPayload())*/ if (!post()) break; 
			try { Thread.sleep(DELAY); } catch (InterruptedException e) {}
		}
		Log.i(TAG, "run over, dereferencing thread");
		failCallback.post(failThread);
		reset();
	}
	
}
