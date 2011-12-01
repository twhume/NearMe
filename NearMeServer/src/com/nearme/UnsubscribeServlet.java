package com.nearme;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * This servlet handles unsubscribing a user from the service,
 * by deleting their address book and user records.
 * 
 * @author twhume
 *
 */

public class UnsubscribeServlet extends GenericNearMeServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5465423067474402780L;
	private static Logger logger = Logger.getLogger(UnsubscribeServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String deviceId = getDeviceId(req);
		UserDAO ud = new UserDAOImpl(datasource);

		try {
			User u = ud.readByDeviceId(deviceId);
			
			/* User doesn't exist? return 404 */
			
			if (u==null) {
				logger.info("unsubscription requested for unknown user " + deviceId);
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {

				ud.deleteUser(u);
				resp.sendError(HttpServletResponse.SC_OK);
				logger.info("unsubscribed user " + u.getId());
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.info("unsubscription failed with " + e);
		}		
	}

}
