package com.nearme;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

/**
 * A PoiFinder class which talks to an SQL database and pulls out lists of Points of Interest
 * 
 * @author twhume
 *
 */

public class DatabasePoiFinder implements PoiFinder {
	private DataSource dataSource = null;
	static Connection conn = null;  
	
	static String bd = "nearme";
	static String login = "root";
	static String password = "123";
	static String url = "jdbc:mysql://localhost/"+bd;
	
	public static void main(String args[]) throws Exception
	   {

		     Class.forName("com.mysql.jdbc.Driver").newInstance(); //load the driver of mysql
	         conn = DriverManager.getConnection(url,login,password); // connect with data base

	         if (conn != null)
	         {
	            System.out.println("Database connection "+url);
	            //conn.close();
	         }

	   }
	
	
	/* Talk to this DataSource to deal with the database */
	
	
	
	public DatabasePoiFinder(DataSource d) {
		this.dataSource = d;
	}

	/**
	 * Pull out a list of Points of Interest matching the contents of the PoiQuery passed in,
	 * and return them in a list.
	 */
	
	public List<Poi> find (PoiQuery pq) {
		//TODO this is where the database magic happens
		
		String value= "Pubs"; //the option in the android
		String var=" ";
		
		try {
			
			
		    Statement st = conn.createStatement(); //use statement to send command to SQL
			
			ResultSet rtype = st.executeQuery("SELECT id FROM type where name='"+value+"';"); //consult(type)
			while (rtype.next()) { 
				var= rtype.getString(1); //get the id of the type
	     	}
			
			ResultSet rs = st.executeQuery("SELECT * FROM poi where type='"+var+"';"); //consult database (poi)
			
			while (rs.next()) //Return false when there is not more data in the table
			{
			   System.out.println(rs.getObject("name")+
			      ", Longitude: "+rs.getObject("longitude")+
			      ", Latitude: "+rs.getObject("latitude")); 
			}
			rs.close();
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}

}
