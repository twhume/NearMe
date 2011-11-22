package com.nearme;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * This servlet handles requests for reading and setting permissions for a given user
 * 
 * @author twhume
 *
 */

public class PermissionsServlet extends GenericNearMeServlet {

	private static final long serialVersionUID = -3524933138212544815L;

	private static Logger logger = Logger.getLogger(PermissionsServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String deviceId = req.getPathInfo();

		UserDAO ud = new UserDAOImpl(datasource);

		try {
			User u = ud.readByDeviceId(deviceId);
			
			/* User doesn't exist? return 404 */
			
			if (u==null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				
				/*
				 * Get all the permissions out, and return them as a JSON String Array
				 */
				
				resp.setContentType("application/json");
				List<IdentityHash> perms = ud.getPermissions(u);
				logger.info("permissions requested for "+ deviceId+": (" + Util.hashListAsString(perms) + ")");
				Gson gson = new Gson();
				resp.getOutputStream().print(gson.toJson(Util.hashListAsStringArray(perms)));
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}		
	}
	
}
