package com.advsofteng.app1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alandonohoe
 * 
 *         This App displays the current time and the device's current GPS co-ordinates. if GPS data is unavailable, it
 *         displays "GPS not available".
 * 
 */
public class NearMeActivity extends MapActivity {

	/* constants */

	private static final int POLL_INTERVAL = (20 * 1000);/* Interval between deliveries of location data to the server*/
	public static final String ENDPOINT = "http://nearme.tomhume.org:8080/NearMeServer";
	public static final String TAG = "LocationPoster"; 	/* used for logging purposes */
	public static final int MAGIC_NUMBER = 12345; 		/* used as a reference to an Alarm */

	/* useful shared objects */

	private TreeMap<String,Integer> idMap;		/* maps indexes in an OverlayItems to IDs of POIs */
	private NearMeApplication app;				/* used for sharing data between BroadcastReceivers and Activities */
	private LocationManager manager;
	private PendingIntent alarmIntent = null; 	/* Handle to the repeatedly called Intent for triggering polls */
	private SharedPreferences prefs = null; 	/* used to share location & time between Activity and BroadcastReceiver */
	MyItemizedOverlay friends, places, me;		/* overlays for the map */
	public static AddressBook globalAddressBook = new AddressBook(); // TODO why is this global?

	/* UI elements */

	private TextView tvGPS; 			/* TextView to show GPS location on-screen */
	private MapView mapView; 			/* On-screen Google Map */
	private List<Overlay> mapOverlays; 	/* List of overlays for the map, obviously */
	private Drawable poiIcon; 			/* variable for the image of places */
	private Drawable friendIcon; 		/* variable for the image of friends */
	private Drawable meIcon; 			/* icon representing my position */

	/*
	 * Slightly fiddly bit here. We register a BroadcastReceiver so that the PoiPoller can tell
	 * this Activity when it's received some data. At this point we know it's time to fresh the map.
	 */
	
	private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateMapData();
		}
	};
	private IntentFilter refreshFilter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (NearMeApplication) getApplicationContext();
		
		setContentView(R.layout.main);
		tvGPS = (TextView) findViewById(R.id.textViewGPS);

		/* map-related set-up */
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setSatellite(true); 			// show Satellite view
		mapView.setBuiltInZoomControls(true); 	// show zoom controls please
		mapView.getController().setZoom(15); 	// zoom quite close: we're looking at street-level stuff
		
		poiIcon = getResources().getDrawable(R.drawable.gmap_blue_icon);
		friendIcon = getResources().getDrawable(R.drawable.google_streets_icon);
		meIcon = getResources().getDrawable(R.drawable.me);

		/* Initialise shared preferences, where we store all sorts of stuff permanently */

		prefs = getSharedPreferences(TAG, Context.MODE_PRIVATE);
		saveDeviceId();

		/* We may know a location even before we start; if so, use it. */

		manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location loc = manager .getLastKnownLocation(LocationManager.GPS_PROVIDER);
		setLocation(loc);

		/* connect the listener object to receive GPS updates */

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new NearMeLocationListener(this));

		refreshFilter = new IntentFilter();
		refreshFilter.addAction("refresh-map");
		
		updateMapData();
	}
	
	/**
	 * This method handles taking the contents of app.getPois() and displaying it as corrently
	 * positioned overlays on the map.
	 */

	private void updateMapData() {
		Log.d(TAG, "NearMeActivity says app.getPois().size==" + app.getPois().size() + ",app="+app);

		friends = new MyItemizedOverlay(friendIcon, this, MyItemizedOverlay.TYPE_FRIENDS);
		places = new MyItemizedOverlay(poiIcon, this, MyItemizedOverlay.TYPE_PLACES);
		me = new MyItemizedOverlay(meIcon, this, MyItemizedOverlay.TYPE_ME);

		mapOverlays = mapView.getOverlays();
		
		idMap = new TreeMap<String,Integer>();
		/**
		 * Work through all the POIs we know about, allocating them either to 
		 * a friends list or a places list
		 */
				
		for (Poi p : app.getPois()) {
			double lat = p.getLatitude();
			double lng = p.getLongitude();
			GeoPoint geopoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			OverlayItem oi = new OverlayItem(geopoint, p.getName(), " "); // " " are the name and the description
			Log.d(TAG, "type="+p.getType());
			if (p.getType().getId() == PoiType.FRIEND) {
				friends.addOverlay(oi);
			} else {
				idMap.put("places-" + places.size(), p.getId());
				Log.d(TAG, "mapping places-"+places.size() + " to " + p.getId());
				places.addOverlay(oi);
			}
		}

		mapOverlays.clear();

		/* Add our own position to the map, if we know it */
		
		if (prefs.getString(PreferencesActivity.KEY_TIME, null)!=null) {
			float lat = Float.parseFloat(prefs.getString(PreferencesActivity.KEY_LAT, ""));
			float lng = Float.parseFloat(prefs.getString(PreferencesActivity.KEY_LNG, ""));
			GeoPoint myPosition = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			me.addOverlay(new OverlayItem(myPosition, getString(R.string.your_position), ""));
			mapOverlays.add(me);
			mapView.getController().animateTo(myPosition);
		}
		
		/* Add our new overlaps to the map */
		
		if (friends.size()>0) mapOverlays.add(friends);
		if (places.size()>0) mapOverlays.add(places);
		mapView.invalidate();
	}

	/**
	 * Updates us with location's longitude and latitude data.
	 * 
	 * @param location
	 * @param tv
	 */

	public void setLocation(Location location) {
		/*
		 * If we get a null location, clear out the preferences and pop up the error message
		 */

		if (null == location) {
			Log.i(TAG, "null location, clearing out");
			emptyPrefs();
			tvGPS.setText(getString(R.string.no_gps_error));
			return;
		}

		String strGPS = String.format(getString(R.string.your_pos), location.getLatitude(), location.getLongitude());
		tvGPS.setText(strGPS);

		// then slap it into those shared preferences so it gets sent up in a poll

		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(PreferencesActivity.KEY_TIME, new Date().toString());
		edit.putString(PreferencesActivity.KEY_LAT,
				Double.toString(location.getLatitude()));
		edit.putString(PreferencesActivity.KEY_LNG,
				Double.toString(location.getLongitude()));
		edit.commit();
		updateMapData();
	}

	private void saveDeviceId() {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(PreferencesActivity.KEY_ID, Secure
				.getString(getApplicationContext().getContentResolver(),
						Secure.ANDROID_ID));
		edit.commit();
	}

	private void emptyPrefs() {
		Log.i(TAG, "emptyPrefs");
		SharedPreferences.Editor edit = prefs.edit();
		edit.remove(PreferencesActivity.KEY_TIME);
		edit.remove(PreferencesActivity.KEY_LAT);
		edit.remove(PreferencesActivity.KEY_LNG);
		edit.commit();

	}

	protected void onPause() {
		unregisterReceiver(refreshReceiver);

		/*
		 * Leaving the app, even briefly? Cancel any subsequent polls and reset button to start
		 */

		if (alarmIntent != null) {
			Log.i(TAG, "starting polling");
			Context ctx = getApplicationContext();
			AlarmManager am = (AlarmManager) ctx
					.getSystemService(Activity.ALARM_SERVICE);
			am.cancel(alarmIntent);
			alarmIntent = null;
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(refreshReceiver, refreshFilter);

		/*
		 * Start the regular polling for POIs
		 */

		if (alarmIntent == null) {
			Log.i(TAG, "starting polling");
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			Calendar cal = Calendar.getInstance();
			Intent intent = new Intent(getApplicationContext(), PoiPoller.class);
			alarmIntent = PendingIntent.getBroadcast(getApplicationContext(),
					MAGIC_NUMBER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					POLL_INTERVAL, alarmIntent);
		}
		
		super.onResume();
	}

	/*
	 * *** implementation of the class MyItemizedOverlay
	 */

	class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		public static final int TYPE_ME = 0;
		public static final int TYPE_FRIENDS = 1;
		public static final int TYPE_PLACES = 2;
		
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private Context context = null;
		private int type = TYPE_ME;
		
		public MyItemizedOverlay(Drawable defaultMarker, Context c, int t) {
			super(boundCenterBottom(defaultMarker));
			this.context = c;
			this.type = t;
		}


		// get item from ArrayList
		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		// get the size
		@Override
		public int size() {
			return mOverlays.size();
		}

		// add item to the list
		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);

			populate();
		}

		public Drawable boundCenterBottomAux(Drawable marker) {
			return boundCenterBottom(marker);
		}

		/**
		 * If an item is tapped, pop up a dialog with its name, asking if you'd like to delete it
		 */
		
		@Override
		protected boolean onTap(final int i) {
			OverlayItem item = mOverlays.get(i);
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(item.getTitle());
			
			if (type==TYPE_PLACES) {				
				dialog.setMessage(getString(R.string.ask_delete));
				dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Log.d(TAG, "delete");
		       		DeletePoiTask deleter = new DeletePoiTask();
		       		Integer idx = idMap.get("places-"+i);
		       		if (idx!=null) {
		       			deleter.execute(idx);
		       		} else Log.d(TAG, "couldn't find index for places-"+i);
		           }
				});
				dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
				});
			} else {
				dialog.setMessage(getString(R.string.for_info));
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			    });
			}
			
			dialog.show();
			return true;
		}



	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Sets up the options menu on-screen
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	/**
	 * Handles clicks on options menu items
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent nextIntent = null;

		switch (item.getItemId()) {
		case R.id.add_poi:
			nextIntent = new Intent(NearMeActivity.this, AddPlaceActivity.class);
			break;
		case R.id.upload_ab:
			nextIntent = new Intent(NearMeActivity.this, AddressBookRipperActivity.class);
			break;
		case R.id.poi_prefs:
			nextIntent = new Intent(NearMeActivity.this, PreferencesActivity.class);
			break;
		}
		if (nextIntent != null) {
			startActivity(nextIntent);
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	private class DeletePoiTask extends AsyncTask<Integer, Integer, Boolean> {

		/**
		 * Once we've finished triggering a delete, tell the user what happened - whether
		 * it was successful or not - and refresh the screen
		 */
		
		protected void onPostExecute(Boolean result) {
			Toast toast;
			if (result) {
				toast = Toast.makeText(getApplicationContext(), getString(R.string.delete_ok), 2000);
			} else {
				toast = Toast.makeText(getApplicationContext(), getString(R.string.delete_fail), 2000);
			}
			updateMapData();
			toast.show();
		}
		
		@Override
		protected Boolean doInBackground(Integer... i) {
			int id = i[0];
			Log.i(TAG, "Delete POI with ID " + id);

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(ENDPOINT + "/poi/delete/" + id);

			try {
				HttpResponse response = client.execute(post);
				Log.i(TAG, "post to " + post.getURI() + " done, response="+response.getStatusLine().getStatusCode());

				/* If we've removed it from the server, then remove it from our list of POIs
				 * on the client, and update the map view
				 */
				
				if (response.getStatusLine().getStatusCode()==200) {
					Poi toRemove = null;
					for (Poi p: app.getPois()) {
						if (p.getId()==id) toRemove = p;
					}
					if (toRemove!=null) {
						app.getPois().remove(toRemove);
					}
					return true;
				}
			} catch (Exception e) {
				Log.i(TAG, "post threw " + e);
				e.printStackTrace();
			}
			return false;

		}
	}
}
	
