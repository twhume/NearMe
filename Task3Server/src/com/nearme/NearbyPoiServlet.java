package com.nearme;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet responds to HTTP GET requests of the form
 * 
 * http://server/nearme/pois/LAT/LONG/RADIUS
 * 
 * where
 * 
 * LAT is a latitude
 * LONG is a longitude
 * RADIUS is a radius (in metres)
 * 
 * and returns a JSON data structure with a list of Points of Interest
 * 
 * @author twhume
 *
 */

public class NearbyPoiServlet extends HttpServlet {

	private static final long serialVersionUID = 4851880984536596503L; // Having this stops Eclipse moaning at us

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		
		PoiQuery pq = new PoiQuery(req.getPathInfo());
		PoiFinder pf = new DatabasePoiFinder(null);
		List<Poi> points = pf.find(pq);
		
	}
	
	

	
	
}
