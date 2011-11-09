package com.nearme;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	
	private DataSource db = null;
	
	/**
	 * On initialisation, pull out a DataSource and save it in a class variable
	 */
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			db = (DataSource)envContext.lookup("jdbc/database");
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new ServletException(ne);
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		PoiQuery pq = new PoiQuery(req.getPathInfo());

/*
  		For now, spit out a list of dummy points, always the same.
  		Later we'll plug the database in and get real ones out.
 
*/		PoiFinder pf = new DatabasePoiFinder(db);
		List<Poi> points = pf.find(pq);

		/* Use GSon to serialise this list onto a JSON structure, and send it to the client.
		 * This is a little bit complicated because we're asking it to serialise a list of stuff;
		 * see the manual at https://sites.google.com/site/gson/gson-user-guide#TOC-Collections-Examples
		 * if you /really/ want to find out why...
		 */
		
		Gson gson = new Gson();
		Type listOfPois = (Type) (new TypeToken<List<Poi>>(){}).getType();
		res.getOutputStream().print(gson.toJson(points, listOfPois));
	}
	
	

	
	
}
