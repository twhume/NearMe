package com.nearme;

/**
 * Interface into the PoiFinder.
 * 
 * This is an interface and not a class so that we can have two implementations: one which we use "for real", which
 * talks to a MySQL database, and another dummy implementation which we can use when constructing unit tests
 * 
 */
import java.sql.SQLException;
import java.util.List;

public interface PoiFinder {
	public List<Poi> find(PoiQuery pq) throws SQLException;
}
