package com.nearme;

import java.util.List;
import javax.sql.DataSource;

/**
 * A PoiFinder class which talks to an SQL database and pulls out lists of Points of Interest
 * 
 * @author twhume
 *
 */

public class DatabasePoiFinder implements PoiFinder {

	/* Talk to this DataSource to deal with the database */
	private DataSource dataSource = null;
	
	public DatabasePoiFinder(DataSource d) {
		this.dataSource = d;
	}

	/**
	 * Pull out a list of Points of Interest matching the contents of the PoiQuery passed in,
	 * and return them in a list.
	 */
	
	public List<Poi> find(PoiQuery pq) {
		//TODO this is where the database magic happens - for Mariana
		
		return null;
	}

}
