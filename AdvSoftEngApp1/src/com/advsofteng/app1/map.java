package com.advsofteng.app1;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class map extends MapActivity {

	private MapView mapView = null;
	GeoPoint geopoint;  
	private Drawable drawableicon; //variable for the image 
	private List<Overlay> mapOverlays;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)  {
		
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.map_poi);
		
		drawableicon = this.getResources().getDrawable(R.drawable.gmap_blue_icon); //set the image
		
		mapView = (MapView)findViewById(R.id.mapView);      		
		
		
	    mapView.setSatellite(true); // show Satellite view
	  	  
	    mapView.setBuiltInZoomControls(true); //method to create the zoom controls of the map
	   
	    mapView.getController().setZoom(4); // assign the initial zoom
	    
	    MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawableicon);
	      
         mapOverlays = mapView.getOverlays();
  
	      
	   	      
		  for (int i = 0; i  < AdvSoftEngApp1Activity.poiArray.size() ; i++) { 
			  
			  double lat = AdvSoftEngApp1Activity.poiArray.get(i).getLatitude();  //get latitude
			  double lng = AdvSoftEngApp1Activity.poiArray.get(i).getLongitude(); //get longitude
			  
			  geopoint = new GeoPoint((int)(lat*1E6), (int)(lng*1E6)); //set the GeoPoint for the map
			  
			 //add item to the map
	          OverlayItem overlayitem = new OverlayItem(geopoint, " " , " "); // " " are the name and the description 
			  
			  itemizedOverlay.addOverlay(overlayitem);
			  
		  } //end for
		  
		  mapOverlays.add(itemizedOverlay); // add all the items to
		  mapView.invalidate();
		
	}
	
	
	/*
	 * *** implementation of the class MyItemizedOverlay 
	 */
	
	public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		public MyItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		//get item from ArrayList
		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}
        
		// get the size
		@Override
		public int size() {
			return mOverlays.size();
		}
        
		//add item to the list
		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    
		    populate();
		}

		public Drawable boundCenterBottomAux(Drawable marker){
			return boundCenterBottom(marker);
		}


		public boolean onTouchEvent(MotionEvent event, MapView mapView)
        {   
            //---when user lifts his finger---
            if (event.getAction() == 1) {                
                GeoPoint p = mapView.getProjection().fromPixels((int) event.getX(),(int) event.getY());
                    Toast.makeText(getBaseContext(), 
                        p.getLatitudeE6() / 1E6 + "," + 
                        p.getLongitudeE6() /1E6 , 
                        Toast.LENGTH_SHORT).show();
            }                            
            return false;
        }   

		

		
		
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}