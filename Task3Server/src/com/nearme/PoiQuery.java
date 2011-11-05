package com.nearme;

/**
 * Encapsulates a query for points of interest.
 * Initially this'll just be lat, long and radius.
 * 
 * Later it might include types of POIs to search for.
 *
 * @author twhume
 *
 */
public class PoiQuery {

	private double latitude;	/* Latitude of the position to search from */
	private double longitude;	/* Longitude of the position to search from */
	private int radius;			/* Metres from the current position to search within */
	
	public PoiQuery(String path) {
		
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	
}
