package com.advsofteng.app1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

// handles user set POI data
public class GetPOIActivity extends Activity { 
	
	////// Member variables /////////
	// CheckBoxes that user selects to get data on from server
	private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
	private SeekBar seekBarRadius; // seekBar that user sets the radius of POIs nearby.
	private TextView tvRadius; // displays the radius data
	private Integer intRadius; // user set radius
	private int intMaxRadius = 1000; // maximum radius in metres
	private Button btnGetPOIdata; // - sends all the data to the server and returns to main screen...
	private Resources resources1; // - used to access app wide resources.
	private SharedPreferences prefs;
	
	// flag used in saving and loading app wide prefs.
	//ref: http://sites.google.com/site/jalcomputing/home/mac-osx-android-programming-tutorial/saving-instance-state
	private Boolean isSavedInstanceState= false; 
	
	// string holding the server address
	private static  String ENDPOINT =null; 
	private static final String tagPOI = "GetPOIActivity"; // used for logging
	//
	///////////////////////////////////
	
	// Called at the start of the full lifetime. 
	@Override 
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState); 
	// Initialize activity.
	
	setContentView(R.layout.get_poi);
	
	// initialise class member data
	resources1 = getResources();
	prefs = getApplicationContext().getSharedPreferences(AdvSoftEngApp1Activity.TAG, Context.MODE_PRIVATE);
	
	// set server address.
	ENDPOINT = getResources().getString(R.string.serverAddress);

	
	//
	if(null != savedInstanceState){ // get saved state from bundle after a soft kill
	    try {
	    	InitialiseCheckBoxsAndBtns(savedInstanceState);
	        Log.i(tagPOI,"RestoredState.");
	    }
	    catch(Exception e){
	        Log.i(tagPOI,"FailedToRestoreState",e);
	    }
	}
	else { // get saved state from preferences on first pass after a hard kill
	    	InitialiseCheckBoxsAndBtns(null);
	       Log.i(tagPOI,"gettingPrefs");
	}
	//
	//////////
	
	
	///////
	// initialise button to respond to clicks with correct action
	btnGetPOIdata = (Button) findViewById(R.id.getPOIDataButton);
	btnGetPOIdata.setOnClickListener(new View.OnClickListener() {
       public void onClick(View v) {
    	  
    	   
    	   //TODO: check with Tom about how the server wants the data on POIs..???
    	   //bool / int POI, how to represent pub and restaurant request... for example???
    	   
    	   /*
    	   // have all data here.... boolean - how to work with database POI request???
    	   checkBox1.isChecked();
    	   checkBox2.isChecked();
    	   checkBox3.isChecked();
    	   checkBox4.isChecked();
    	   checkBox5.isChecked();
    	  
    	   // and each checkBox's text... pub, park, etc...
    	   resources1.getText((R.string.poi_checkbox1));
    	   resources1.getText((R.string.poi_checkbox2));
    	   resources1.getText((R.string.poi_checkbox3));
    	   resources1.getText((R.string.poi_checkbox4));
    	   resources1.getText((R.string.poi_checkbox5));
    	   
    	   // and user set radius data...
    	   // intRadius;
    	    */
   			try {

   	   			
   				if(null != prefs.getString("time", null)) // if we actually get a latitude value back.. then 
   				{												 //  the GPS is working proceed... else....
   					
   					HttpGet get = new HttpGet(ENDPOINT + "/" + prefs.getString("latitude", "") 
   	   												+ "/" + prefs.getString("longitude", "")
   	   												+ "/" + intRadius.toString()
   	   												+ "/" + "1");
   	   												
   					//TODO: remove this testing block once we know we are getting data from the prefs
   					//testing to see if we are getting long and lat data...  
   					Log.i(tagPOI, "Long from prefs = "+ prefs.getString("longitude", ""));
   					Log.i(tagPOI, "lat from prefs = " + prefs.getString("latitude", ""));
   					Log.i(tagPOI, "Mainprefs = " + prefs.toString());

   					/* Create a new HTTPClient to do our POST for us */
   					HttpClient client = new DefaultHttpClient();
   					HttpResponse response = client.execute(get);

   					//TODO: finalise this code once we know we are getting the correct data back from the HTTP request.

   					//1.a: get text out of body of response....
   					String responseBody = HttpHelper.getResponseBody(response);
   					
   					// 1.b: get the Poi objects out of the string....
   					// ref: http://benjii.me/2010/04/deserializing-json-in-android-using-gson/
   					
   					//GsonBuilder gsonb = new GsonBuilder();
   					Gson gson = new Gson();// gsonb.create();
   					
   					JSONObject j;
   					Poi poiTest = null;
   					
   					try
   					{
   						j = new JSONObject(responseBody);
   						poiTest = gson.fromJson(j.toString(), Poi.class);
   					}
   					catch(Exception e)
   					{
   						e.printStackTrace();
   					}

   				} 
   				else { 
   					// we have NOT got GPS data.....
   					Toast toast=Toast.makeText(getApplicationContext(), resources1.getString(R.string.no_gps_error), 2000);
   					toast.setGravity(Gravity.TOP, -30, 50);
   					toast.show();
   				}
   				
   			} catch (Exception e) {
   				Log.i(AdvSoftEngApp1Activity.TAG, "get to getPOI failed, " + e.getMessage());
   				
   			}
   			
       finish(); // return to previous screen...
   			
       } } );

	}
	
	// create and instantiate and override implemented seekbar methods...
	private SeekBar.OnSeekBarChangeListener seekBarListen = new SeekBar.OnSeekBarChangeListener() {
		
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			
			// update member variable and textview with new radius data...
			intRadius = progress;
			tvRadius = (TextView) findViewById(R.id.tvRadius);
			tvRadius.setText(resources1.getText((R.string.tvRadiusText)) + Integer.toString(intRadius) + " m");
			
		}
	};
	
	// Called after onCreate has finished, use to restore UI state 
	@Override 
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState); 
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.

		if(savedInstanceState != null){

			intRadius = savedInstanceState.getInt((String)resources1.getText(R.string.tvRadiusText), 0);
		}

	}
	
	 
	private void InitialiseCheckBoxsAndBtns(Bundle lastInstanceState)
	{
		
		// CheckBox setup...
		checkBox1 = (CheckBox) findViewById(R.id.CheckBox1);
		checkBox2 = (CheckBox) findViewById(R.id.CheckBox2);
		checkBox3 = (CheckBox) findViewById(R.id.CheckBox3);
		checkBox4 = (CheckBox) findViewById(R.id.CheckBox4);

		// set text....
		checkBox1.setText(resources1.getText(R.string.poi_checkbox1));
		checkBox2.setText(resources1.getText(R.string.poi_checkbox2));
		checkBox3.setText(resources1.getText(R.string.poi_checkbox3));
		checkBox4.setText(resources1.getText(R.string.poi_checkbox4));
	
		/////////////////
		//seekBar and corresponding label set up
		seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
		seekBarRadius.setOnSeekBarChangeListener(seekBarListen);
		seekBarRadius.setMax(intMaxRadius);

		
		///////////////////////////////////////////////////////
		// everything below is called when this activity is paused and then resumed,
		// rather than when starting the activity from scratch
		boolean booleanFlag = false; // used as a temp boolean value holder below
		if(null == lastInstanceState)
		{
			//get checkbox1's last state from saved prefs....
			booleanFlag = prefs.getBoolean((String)resources1.getText((R.string.poi_checkbox1)), booleanFlag);
			checkBox1.setChecked(booleanFlag);
			
			//get checkbox2's last state from saved prefs....
			booleanFlag = prefs.getBoolean((String)resources1.getText((R.string.poi_checkbox2)), booleanFlag);
			checkBox2.setChecked(booleanFlag);
			
			//get checkbox3's last state from saved prefs....
			booleanFlag = prefs.getBoolean((String)resources1.getText((R.string.poi_checkbox3)), booleanFlag);
			checkBox3.setChecked(booleanFlag);
			
			//get checkbox4's last state from saved prefs....
			booleanFlag = prefs.getBoolean((String)resources1.getText((R.string.poi_checkbox4)), booleanFlag);
			checkBox4.setChecked(booleanFlag);
			
			// get radius value...
			intRadius = prefs.getInt((String)resources1.getText(R.string.tvRadiusText), 0);
		}
		else //If lastInstanceState != null, then we are to use preferences to load data...
		{
			if(null != lastInstanceState)
				intRadius = lastInstanceState.getInt((String)resources1.getText(R.string.tvRadiusText), 0);		
		}
		
		seekBarRadius.setProgress(intRadius);

		//radius label
		tvRadius = (TextView) findViewById(R.id.tvRadius);
		tvRadius.setText(resources1.getText((R.string.tvRadiusText)) + Integer.toString(intRadius) + " m");
		

		
	}

	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
		isSavedInstanceState= true;
		
		savedInstanceState.putBoolean((String)resources1.getText((R.string.poi_checkbox1)), checkBox1.isChecked());
		savedInstanceState.putBoolean((String)resources1.getText((R.string.poi_checkbox2)), checkBox2.isChecked());
		savedInstanceState.putBoolean((String)resources1.getText((R.string.poi_checkbox3)), checkBox3.isChecked());
		savedInstanceState.putBoolean((String)resources1.getText((R.string.poi_checkbox4)), checkBox4.isChecked());
		
		savedInstanceState.putInt((String)resources1.getText(R.string.tvRadiusText), intRadius);
		
		super.onSaveInstanceState(savedInstanceState);

	}

    protected void onStart() {
        Log.i(tagPOI,"onStart");
        
        InitialiseCheckBoxsAndBtns(null);
        super.onStart();
    }
    protected void onResume() {
        Log.i(tagPOI,"onResume");
    	isSavedInstanceState= false;
        super.onResume();
    }
    protected void onStop() {
    	
    	if (!isSavedInstanceState){ // this is a HARD KILL, write to prefs
      
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putBoolean((String)resources1.getText(R.string.poi_checkbox1), checkBox1.isChecked());
            editor.putBoolean((String)resources1.getText(R.string.poi_checkbox2), checkBox2.isChecked());
            editor.putBoolean((String)resources1.getText(R.string.poi_checkbox3), checkBox3.isChecked());
            editor.putBoolean((String)resources1.getText(R.string.poi_checkbox4), checkBox4.isChecked());
            editor.putInt((String)resources1.getText(R.string.tvRadiusText), intRadius);
            
            editor.commit();
            Log.i(tagPOI,"savedPrefs");
            
        }
        else {
            Log.i(tagPOI,"DidNotSavePrefs");
        }
        super.onStop();
    }

}