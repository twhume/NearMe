package com.nearme;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the receipt of a JSON data structure containing the address book for a given user,
 * and saves it into the local database - removing any previous entries for that user.
 * @author twhume
 *
 */

public class AddressBookServlet extends NearMeServlet {

	private static final long serialVersionUID = -1593856664522697355L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		UserDAO uDao = new UserDAOImpl(datasource);

		String s = convertStreamToString(req.getInputStream());
		POSTedAddressBookParser pabp = new POSTedAddressBookParser();
		pabp.parse(s);
				
		//TODO take the User and address book we have received and add them into the database
		// then return a 200 code
		// otherwise return a 500 and log an error
		
		System.err.println(s);
		resp.setContentType("text/plain");
		resp.getOutputStream().println("<h1>Thanks</h1>");
	}
	
	/**
	 * Method cribbed from http://stackoverflow.com/questions/309424/in-java-how-do-a-read-convert-an-inputstream-in-to-a-string
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}

}
