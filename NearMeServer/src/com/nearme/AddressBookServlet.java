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

public class AddressBookServlet extends HttpServlet {

	private static final long serialVersionUID = -1593856664522697355L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String s = convertStreamToString(req.getInputStream());
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
