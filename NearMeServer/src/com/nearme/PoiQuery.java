package com.nearme;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

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
	
	private static Logger logger = Logger.getLogger(PoiQuery.class);

	private String androidId;			/* Unique ID of the device making the PoiQuery */
	private double latitude;			/* Latitude of the position to search from */
	private double longitude;			/* Longitude of the position to search from */
	private int radius;					/* Metres from the current position to search within */
	private ArrayList<Integer> types;	/* List of Integers corresponding to the type IDs we are interested in */
	
	public PoiQuery(String path) {
		try {
			StringTokenizer st = new StringTokenizer(path, "/?=");
			this.androidId = st.nextToken();
			this.latitude = Double.parseDouble(st.nextToken());
			this.longitude = Double.parseDouble(st.nextToken());
			this.radius = Integer.parseInt(st.nextToken());
			this.types = new ArrayList<Integer>();

			/**
			 * Now parse any ?t= suffix at the end of the URL to extract a list of requested types
			 */
			
			if (st.hasMoreTokens()) {
				String nextTok = st.nextToken();
				if (nextTok.equals("t")) {
					StringTokenizer typeTok = new StringTokenizer(st.nextToken(), ",");
					while (typeTok.hasMoreTokens()) {
						nextTok = typeTok.nextToken();
						types.add(new Integer(Integer.parseInt(nextTok)));
					}
				} else logger.warn("unexpected token " + nextTok);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("parsing " + path + " threw " + e);
		}
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
	public ArrayList<Integer> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<Integer> types) {
		this.types = types;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}
	
	
}
