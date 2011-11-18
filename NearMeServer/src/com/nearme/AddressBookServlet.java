package com.nearme;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Handles the receipt of a JSON data structure containing the address book for a given user,
 * and saves it into the local database - removing any previous entries for that user.
 * @author twhume
 *
 */

public class AddressBookServlet extends GenericNearMeServlet {

	private static final long serialVersionUID = -1593856664522697355L;

	private static Logger logger = Logger.getLogger(AddressBookServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		UserDAO uDao = new UserDAOImpl(datasource);

		String s = convertStreamToString(req.getInputStream());
		POSTedAddressBookParser pabp = new POSTedAddressBookParser();
		pabp.parse(s);
		
		logger.info("received address book from " + pabp.getUser().getId() + " with " + pabp.getBook().size() + " entries");
		try {
			User u = uDao.write(pabp.getUser());
			uDao.setAddressBook(u.getId(), pabp.getBook());
			resp.sendError(HttpServletResponse.SC_OK);
			logger.debug("parsed address book OK");
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
			
	}
	
	/**
	 * Method cribbed from http://stackoverflow.com/questions/309424/in-java-how-do-a-read-convert-an-inputstream-in-to-a-string
	 * TODO: move into a proper utility class
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}

}
