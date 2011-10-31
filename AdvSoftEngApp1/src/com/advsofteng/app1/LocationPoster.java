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

/**
 * Class which actions delivery of a location and timestamp to a server
 * 
 * @author twhume
 *
 */
public class LocationPoster extends Thread {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://localhost:8080/";
	
	/* How long to wait between sending locations to the server */
	private static final int DELAY = (5 * 60 * 1000); // every 5 minutes
	
	private HttpClient client = null;
	private List<NameValuePair> payload = null;
	private boolean finishSignalled = false;
	
	public LocationPoster() {
		client = new DefaultHttpClient();
		reset();
	}
	
	private void reset() {
		payload = new ArrayList<NameValuePair>(); 
		finishSignalled = false;
	}
	
	public void finish() {
		this.finishSignalled = true;
	}

	public void set(String name, String value) {
		payload.add(new BasicNameValuePair(name, value));
	}
	
	public void post() {
	    HttpPost post = new HttpPost(ENDPOINT);

	    try {
	        post.setEntity(new UrlEncodedFormEntity(payload));
	        HttpResponse response = client.execute(post);
	        reset(); /* clear out data after a poll */
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	/* In the event of any problems, stop polling */
	    	finish();
	    }
	}
	
	private boolean validPayload() {
		if (payload==null) return false;
		if (payload.size()!=3) return false;

		//TODO: add routine to check valid names in payload
		
		return true;
	}
	
	public void run() {
		while (!finishSignalled) {
			/* Post up data to the server, but only if we have a time, latitude and longitude */
			if (validPayload()) post(); 
			try { Thread.sleep(DELAY); } catch (InterruptedException e) {}
		}
	}
	
}
