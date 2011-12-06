package com.nearme;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class DeletePOIServlet
 */
public class DeletePOIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 
	private int id;
	static Connection conn = null;
	static String bd = "nearme";
	static String login = "nearme";
	static String password = "nearme";
	static String url = "jdbc:mysql://localhost/" + bd;
	
	private static Logger logger = Logger.getLogger(DeletePOIServlet.class);
	
	public static void main(String args[]) throws Exception {

		Class.forName("com.mysql.jdbc.Driver").newInstance(); // load the driver of mysql
		

		
	}
	
	public DeletePOIServlet() {
        super();
        
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost (request, response);
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		id = Integer.parseInt(request.getParameter("id")); //get the id of the poi that will be delete
		System.out.println(id);	
		try
		  {
			conn = DriverManager.getConnection(url, login, password); // connect with data base
			Statement st = (Statement) conn.createStatement();
            String sql = "Delete from poi Where Id =" + id;
			st.executeUpdate(sql);

						
			logger.info("Delete POI " + id);
            st.close();
			//System.out.println("POI Deleted");
			conn.close();
			
		  }
		  catch (SQLException e) {
			  e.printStackTrace(); 
		  }
	
		
		response.setContentType("text/plain");
		response.getOutputStream().println("OK");
	
	
	}

}
