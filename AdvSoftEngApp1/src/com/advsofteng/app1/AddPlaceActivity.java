package com.advsofteng.app1;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddPlaceActivity extends Activity {

	/* Endpoint to which HTTP POSTs should go */
	private static final String ENDPOINT = "http://nearme.tomhume.org:8080/NearMeServer/AddPOIServlet";


	private TextView latitudeField;
	private TextView longitudeField;
	private double lat;
	private double lng;

	private SharedPreferences prefs;
	
	private EditText name;

	private Button Add = null;
	private Button close_button = null;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState)  {

		super.onCreate(savedInstanceState); 
		setContentView(R.layout.add_poi);

		/*fill in the spinner for type with the xml document */
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);   
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Types, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spinner.setAdapter(adapter);

		latitudeField = (TextView) findViewById(R.id.TextViewLAT); // textView for latitude
		longitudeField = (TextView) findViewById(R.id.TextViewLON); // textView for longitude 
		name = (EditText) findViewById(R.id.editText1);

		//call the function that get the value of EditText
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener()); 

		prefs = getSharedPreferences(NearMeActivity.TAG, Context.MODE_PRIVATE);

		
		// Deal with the button add
		Add = (Button) findViewById(R.id.button_add);
		Add.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				name = (EditText) findViewById(R.id.editText1);

				//TODO this is checking lat and long are zero; but zero is valid. Replace with a haveLocation flag set by the LocationListener
				if (lat == 0 && lng ==0)
				{	  
					Toast.makeText( getApplicationContext(), "The Latitude and Longitude are unknown",Toast.LENGTH_SHORT).show();
				}
				else
				{	  
					if (name.getText().toString().equals("")) // check that the name has been inserted
					{
						Toast.makeText( getApplicationContext(), "Please, Insert the name.",Toast.LENGTH_SHORT).show();
					}
					else
					{
						//***** send all the parameter to the addPOIservlet (lat, lng, name, type)
						HttpClient client = new DefaultHttpClient();
						HttpPost post = new HttpPost(ENDPOINT);

						try
						{

							ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("lat", Double.toString(lat))); 
							params.add(new BasicNameValuePair("lng", Double.toString(lng))); 
							params.add(new BasicNameValuePair("name", name.getText().toString()));
							params.add(new BasicNameValuePair("type", type));

							post.setEntity(new UrlEncodedFormEntity(params));
							client.execute(post);				
							//TODO externalise strings into strings.xml
							Toast.makeText(getApplicationContext(), "New POI has been saved", Toast.LENGTH_LONG).show();

							finish(); // return to previous screen...

						}
						catch (Exception e)
						{
							e.printStackTrace();
							Log.e(NearMeActivity.TAG, "Error posting a new place: " + e);
						}

					}
				} // end if lat and lng 
			} // end of onClickView(View v)
		}//  end of View.OnClickListener
				); // end of setOnClickListener



		// Deal with the button close
		close_button = (Button) findViewById(R.id.Close);
		close_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				finish(); // return to previous screen...
			}
		});




	}	// end onCreate



	/*
	 *  Class to deal with the spinner 
	 */
	public class MyOnItemSelectedListener implements OnItemSelectedListener { 

		public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) 
		{   
			type = parent.getItemAtPosition(pos).toString();
		}    
		public void onNothingSelected(AdapterView<?> parent) {   
			// Do nothing
		}

	}



	@Override
	protected void onResume() {
		Log.d(NearMeActivity.TAG, "resuming");
		if (prefs.getString(PreferencesActivity.KEY_TIME, null)!=null) {
			lat = Double.parseDouble(prefs.getString(PreferencesActivity.KEY_LAT, ""));
			lng = Double.parseDouble(prefs.getString(PreferencesActivity.KEY_LNG, ""));
			latitudeField.setText(""+lat);
			longitudeField.setText("" +lng);
		} else {
			lat = 0;
			lng = 0;
			latitudeField.setText("unknown");
			longitudeField.setText("unknown");
			//FIXME these are valid lats and longs, should be fixed
		}
		Log.d(NearMeActivity.TAG, "resuming, coords="+lat+","+lng);
		super.onResume();
	}



} //end addPlace