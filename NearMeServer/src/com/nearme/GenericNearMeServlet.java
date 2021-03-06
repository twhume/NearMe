package com.nearme;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * Abstract class for Servlets; every servlet we use in this project will need 
 * to interact with a database, this one includes some methods to help with that.
 * 
 * @author twhume
 *
 */

public abstract class GenericNearMeServlet extends HttpServlet {

	private static final long serialVersionUID = -100216811410495886L;
	protected DataSource datasource = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			datasource = (DataSource)envContext.lookup("jdbc/database");
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new ServletException(ne);
		}
	}

	/**
	 * Helper method, returns the device ID from this request.
	 * Returns null if it can't find one.
	 * 
	 * @param req
	 * @return
	 */
	protected String getDeviceId(HttpServletRequest req) {
		String deviceId = req.getPathInfo();
		if ((deviceId==null) || (deviceId.length()<2)) return null;
		
		return deviceId.substring(1, deviceId.length());
		
	}

}
