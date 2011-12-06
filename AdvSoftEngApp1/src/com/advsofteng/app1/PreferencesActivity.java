package com.advsofteng.app1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

/**
 * Lets the user say which POIs they are interested in seeing, and at what
 * radius. Saves these details into shared preferences so that they can be
 * accessed by other classes (e.g. PoiPollingService) at any time later on.
 * 
 * @author twhume
 * 
 */

public class PreferencesActivity extends Activity {

	/* Maximum possible radius of interest */
	private static final int MAX_RADIUS = 1000;	
	
	/* These are keys used to refer to values stored in shared preferences,
	 * so that we don't misspell key names or use them inconsistently
	 */
	
	public static final String KEY_TIME = "time";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LNG = "longitude";
	public static final String KEY_RADIUS = "radius";
	public static final String KEY_TYPES = "types";
	public static final String KEY_ID = "device-id";
	

	// CheckBoxes that user selects to get data on from server
	private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
	private SeekBar seekBarRadius; 		// slider to choose radius of interest
	private TextView tvRadius; 			// textview displaying the current radius
	private Integer intRadius; 			// current radius of interest
	private Button savePrefsButton;
	private SharedPreferences prefs;

	// flag used in saving and loading app wide prefs.
	// http://sites.google.com/site/jalcomputing/home/mac-osx-android-programming-tutorial/saving-instance-state
	private Boolean isSavedInstanceState = false;

	// create and instantiate and override implemented seekbar methods...
	private SeekBar.OnSeekBarChangeListener seekBarListen = new SeekBar.OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {}

		public void onStartTrackingTouch(SeekBar seekBar) {}

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			/* update member variable and textview with new radius data */
			intRadius = progress;
			tvRadius = (TextView) findViewById(R.id.tvRadius);
			tvRadius.setText(getResources().getText((R.string.tvRadiusText)) + " " + Integer.toString(intRadius) + "m");
		}
	};
	
	// Called at the start of the full lifetime.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.get_poi);
		prefs = getApplicationContext().getSharedPreferences(AdvSoftEngApp1Activity.TAG, Context.MODE_PRIVATE);

		/* If we've been soft-killed, retrieve saved state from the bundle */

		if (null != savedInstanceState) {
			try {
				InitialiseCheckBoxsAndBtns(savedInstanceState);
				Log.i(AdvSoftEngApp1Activity.TAG, "RestoredState.");
			} catch (Exception e) {
				Log.i(AdvSoftEngApp1Activity.TAG, "FailedToRestoreState", e);
			}
		} else { /*
				 * get saved state from preferences on first pass after a hard
				 * kill
				 */
			InitialiseCheckBoxsAndBtns(null);
			Log.i(AdvSoftEngApp1Activity.TAG, "gettingPrefs");
		}

		/* When the savePrefsButton is clicked, saved preferences and leave the Activity */
		
		savePrefsButton = (Button) findViewById(R.id.getPOIDataButton);
		savePrefsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(AdvSoftEngApp1Activity.TAG, "save POI preferences");
				saveStateToPreferences(prefs);
				finish();
			}
		});
	}

	/* Called after onCreate has finished, use to restore UI state */

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			retrieveStateFromBundle(savedInstanceState);
		}
	}

	private void InitialiseCheckBoxsAndBtns(Bundle lastInstanceState) {

		// CheckBox setup...
		checkBox1 = (CheckBox) findViewById(R.id.CheckBox1);
		checkBox2 = (CheckBox) findViewById(R.id.CheckBox2);
		checkBox3 = (CheckBox) findViewById(R.id.CheckBox3);
		checkBox4 = (CheckBox) findViewById(R.id.CheckBox4);

		// seekBar and corresponding label set up
		seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
		seekBarRadius.setOnSeekBarChangeListener(seekBarListen);
		seekBarRadius.setMax(MAX_RADIUS);

		if (null == lastInstanceState) retrieveStateFromPreferences(prefs);
		else retrieveStateFromBundle(lastInstanceState);

		// radius label
		tvRadius = (TextView) findViewById(R.id.tvRadius);
		tvRadius.setText(getResources().getText((R.string.tvRadiusText)) + " " + Integer.toString(intRadius) + "m");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		isSavedInstanceState = true;
		saveStateToBundle(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}

	protected void onStart() {
		InitialiseCheckBoxsAndBtns(null);
		super.onStart();
	}

	protected void onResume() {
		isSavedInstanceState = false;
		super.onResume();
	}

	protected void onStop() {
		if (!isSavedInstanceState)
			saveStateToPreferences(prefs);
		super.onStop();
	}

	private void saveStateToPreferences(SharedPreferences p) {
		Log.d(AdvSoftEngApp1Activity.TAG, "saveStateToPreferences()");
		SharedPreferences.Editor editor = p.edit();
		editor.putBoolean(getResources().getString(R.string.poi_checkbox1),
				checkBox1.isChecked());
		editor.putBoolean(getResources().getString(R.string.poi_checkbox2),
				checkBox2.isChecked());
		editor.putBoolean(getResources().getString(R.string.poi_checkbox3),
				checkBox3.isChecked());
		editor.putBoolean(getResources().getString(R.string.poi_checkbox4),
				checkBox4.isChecked());
		editor.putInt(KEY_RADIUS, intRadius);
		editor.putString(KEY_TYPES, getTypesStringFromCheckboxes());
		editor.commit();
	}

	private void saveStateToBundle(Bundle b) {
		Log.d(AdvSoftEngApp1Activity.TAG, "saveStateToBundle()");
		b.putBoolean(getResources().getString(R.string.poi_checkbox1),
				checkBox1.isChecked());
		b.putBoolean(getResources().getString(R.string.poi_checkbox2),
				checkBox2.isChecked());
		b.putBoolean(getResources().getString(R.string.poi_checkbox3),
				checkBox3.isChecked());
		b.putBoolean(getResources().getString(R.string.poi_checkbox4),
				checkBox4.isChecked());
		b.putString(KEY_TYPES, getTypesStringFromCheckboxes());
		b.putInt(KEY_RADIUS, intRadius);
	}

	private void retrieveStateFromBundle(Bundle b) {
		Log.d(AdvSoftEngApp1Activity.TAG, "retrieveStateFromBundle() b="+b);
		checkBox1.setChecked(b.getBoolean(
				getResources().getString(R.string.poi_checkbox1), false));
		checkBox2.setChecked(b.getBoolean(
				getResources().getString(R.string.poi_checkbox2), false));
		checkBox3.setChecked(b.getBoolean(
				getResources().getString(R.string.poi_checkbox3), false));
		checkBox4.setChecked(b.getBoolean(
				getResources().getString(R.string.poi_checkbox4), false));
		intRadius = b.getInt(KEY_RADIUS, 0);
		seekBarRadius.setProgress(intRadius);
	}

	private void retrieveStateFromPreferences(SharedPreferences p) {
		Log.d(AdvSoftEngApp1Activity.TAG, "retrieveStateFromPreferences() p="+p);
		checkBox1.setChecked(p.getBoolean(
				getResources().getString(R.string.poi_checkbox1), false));
		checkBox2.setChecked(p.getBoolean(
				getResources().getString(R.string.poi_checkbox2), false));
		checkBox3.setChecked(p.getBoolean(
				getResources().getString(R.string.poi_checkbox3), false));
		checkBox4.setChecked(p.getBoolean(
				getResources().getString(R.string.poi_checkbox4), false));
		intRadius = p.getInt(KEY_RADIUS, 0);
		seekBarRadius.setProgress(intRadius);
	}
	
	private String getTypesStringFromCheckboxes() {
		String tmpStr = "";
		if (checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked()) {
			if (checkBox1.isChecked()) tmpStr = tmpStr + "1,";
			if (checkBox2.isChecked()) tmpStr = tmpStr + "2,";
			if (checkBox3.isChecked()) tmpStr = tmpStr + "3,";
			if (checkBox4.isChecked()) tmpStr = tmpStr + "4,";
			tmpStr = tmpStr.substring(0, tmpStr.length()-1);
		}
		return tmpStr;	
	}
}
