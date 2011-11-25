package com.advsofteng.app1;


import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class map extends MapActivity {

	private MapView mapView = null;
	private MapController mapController; 
	GeoPoint geopoint;
	

	class MapOverlay extends com.google.android.maps.Overlay
    {
		
		@Override
		
        public boolean draw(Canvas canvas, MapView mapView,boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(geopoint, screenPts);
 
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.gmap_blue_icon);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
            //return true;
			return shadow;
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
	public void onCreate(Bundle savedInstanceState)  {
		
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.map_poi);
		
		
		mapView = (MapView)findViewById(R.id.mapView);      		
		
		
	    mapView.setSatellite(true); // show Satellite view
	  	  
	    mapView.setBuiltInZoomControls(true); //method to create the zoom controls of the map
		  

		/*  p1 = new GeoPoint(19240000,-99120000);
		  p1 = new GeoPoint(20250000,-97140000);
		  p1 = new GeoPoint(19470000,-99010000); */
		  
		/*  type newtype = new type(51.5001524, -0.1262362, "London", "City");
		  type newtype2 = new type(50.819522, -0.13642, "Brighton", "City");
		  Pointlist.add(newtype);
		  Pointlist.add(newtype2);
		  */
		  
		  
		  
	      MapOverlay mapOverlay = new MapOverlay ();
	      List<Overlay> listOfOverlays = mapView.getOverlays();
	      listOfOverlays.clear();
	      mapController = mapView.getController();
	      mapController.setZoom(9);
	      
	   	      
		  for (int i = 0; i  < AdvSoftEngApp1Activity.poiArray.size() ; i++) { 
			  
			  double lat = AdvSoftEngApp1Activity.poiArray.get(i).getLatitude();  //get latitude
			  double lng = AdvSoftEngApp1Activity.poiArray.get(i).getLongitude(); //get longitude
			  
			  geopoint = new GeoPoint((int)(lat*1E6), (int)(lng*1E6)); 
			  
			  mapController.animateTo(geopoint);

			  listOfOverlays.add(mapOverlay);
			  mapView.invalidate();

		  } 
		
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}