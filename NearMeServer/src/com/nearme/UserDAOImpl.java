package com.nearme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class UserDAOImpl implements UserDAO {

	/* In the real world I'd be using an ORM here, but that feels like one too many things
	 * to shove at the team during this project. 
	 * 
	 * Actually I'd probably write the thing in Groovy or Perl, but that'd be even crueller.
	 */
	private static final String READ_ID_SQL = "SELECT user.*, idHash.hash FROM user,idHash WHERE user.id = ? AND idHash.id = user.hashId";
	private static final String READ_HASH_SQL = "SELECT user.*, idHash.hash FROM user,idHash WHERE idHash.hash = ? AND idHash.id = user.hashId";
	private static final String READ_DEVICE_SQL = "SELECT user.*, idHash.hash FROM user,idHash WHERE user.deviceId = ? AND idHash.id = user.hashId";
	private static final String READ_AB_SQL = "SELECT ab.id, ab.name, ab.permission, ih.id, ih.hash FROM addressBook ab, addressBookHashMatcher abhm, idHash ih WHERE ab.ownerId = ? AND abhm.addressBookId = ab.id AND abhm.hashId = ih.id ORDER BY ab.name";
	private static final String NEAREST_SQL = "SELECT u2.*, ab.name, ( 6371 * acos( cos( radians(-123.45) ) * cos( radians( u2.latitude ) ) * cos( radians( u2.longitude ) - radians(-45.37) ) + sin( radians(-123.45) ) * sin( radians( u2.latitude ) ) ) ) AS distance FROM user u, addressBook ab, addressBookHashMatcher abhm, user u2 WHERE u.id = 1 AND u.id = ab.ownerId AND abhm.addressBookId = ab.id AND abhm.hashId = u2.hashId  HAVING distance < ? ORDER BY distance";

	private static final String USER_INSERT_SQL = "INSERT INTO user (deviceId, hashId, latitude, longitude, lastReport) VALUES (?,?,?,?,?)";
	private static final String USER_UPDATE_POSITION_SQL = "UPDATE user SET latitude = ?, longitude = ?, lastReport = ? WHERE id = ?";
	private static final String USER_UPDATE_IDHASH_SQL = "UPDATE user SET hashId = ? WHERE id = ?";
	private static final String IDHASH_FIND_SQL = "SELECT id FROM idHash WHERE hash = ?";
	private static final String IDHASH_INSERT_SQL = "INSERT INTO idHash (hash) VALUES (?)";
	
	private DataSource dataSource = null;
	
	public UserDAOImpl(DataSource d) {
		this.dataSource = d;
	}

	/**
	 * Read a User from the database, identified by their unique ID
	 */
	
	@Override
	public User read(int i) throws SQLException {
		Connection c = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			c = dataSource.getConnection();
			pst = c.prepareStatement(READ_ID_SQL);
			pst.setInt(1, i);
			rs = pst.executeQuery();
			if (rs.next()) return userFrom(rs);
			else return null;
		} finally {
			if (rs!=null) rs.close();
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
	}
	
	/**
	 * Helper method, takes a ResultSet and returns the first User from it
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	
	private User userFrom(ResultSet rs) throws SQLException {
		User u = new User(rs.getInt("user.id"),
					rs.getString("user.deviceId"),
					rs.getString("idHash.hash"));
		
		if (rs.getTimestamp("user.lastReport")!=null)
			u.setLastPosition(new Position(rs.getDouble("user.latitude"), rs.getDouble("user.longitude"), new java.util.Date(rs.getTimestamp("user.lastReport").getTime())));
		
		return u;
	}

	/**
	 * Read a User from the database, identified by the Hash of their MSISDN
	 */

	public User readByHash(String string) throws SQLException {
		Connection c = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			c = dataSource.getConnection();
			pst = c.prepareStatement(READ_HASH_SQL);
			pst.setString(1, string);
			rs = pst.executeQuery();
			if (rs.next()) return userFrom(rs);
			else return null;
		} finally {
			if (rs!=null) rs.close();
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
	}

	/**
	 * Read a User from the database, identified by their unique device ID
	 */

	@Override
	public User readByDeviceId(String string) throws SQLException {
		Connection c = null;
		PreparedStatement pst = null;
		try {
			c = dataSource.getConnection();
			pst = c.prepareStatement(READ_DEVICE_SQL);
			pst.setString(1, string);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) return userFrom(rs);
			else return null;
		} finally {
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
	}

	/**
	 * Read the address book for a user from the database, identified by their unique ID
	 */

	@Override
	public List<AddressBookEntry> getAddressBook(int i) throws SQLException {
		
		User u = read(i);
		if (u==null) return null;
		
		Connection c = null;
		PreparedStatement pst = null;
		ArrayList<AddressBookEntry> ret = new ArrayList<AddressBookEntry>();
		try {
			c = dataSource.getConnection();
			pst = c.prepareStatement(READ_AB_SQL);
			pst.setInt(1, i);
			ResultSet rs = pst.executeQuery();
			int lastId = -1;
			AddressBookEntry abe = null;
			ArrayList<IdentityHash> hashes = null;
			
			while (rs.next()) {
				// rows of ab.id, ab.name, ab.permission, ih.id, ih.hash
				
				IdentityHash ih = new IdentityHash(rs.getInt("ih.id"), rs.getString("ih.hash"));
				if (rs.getInt("ab.id")!=lastId) {
					hashes = new ArrayList<IdentityHash>();
					abe = new AddressBookEntry(rs.getInt("ab.id"), u, rs.getString("ab.name"), rs.getInt("ab.permission"), hashes);
					ret.add(abe);
				}
				hashes.add(ih);
			}
		} finally {
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
		return ret;
	}

	@Override
	public List<Poi> getNearestUsers(User u, int radius) throws SQLException {
		Connection c = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<Poi> ret = new ArrayList<Poi>();
		try {
			c = dataSource.getConnection();
			pst = c.prepareStatement(NEAREST_SQL);
			pst.setDouble(1, u.getLastPosition().getLatitude());
			pst.setDouble(2, u.getLastPosition().getLongitude());
			pst.setDouble(3, u.getLastPosition().getLatitude());
			pst.setInt(4, u.getId());
			pst.setInt(5, radius);

			rs = pst.executeQuery();
			while (rs.next()) {
				Poi p = new Poi(rs.getString("ab.name"), rs.getDouble("u2.latitude"), rs.getDouble("u2.longitude"), PoiType.FRIEND, 0);
				ret.add(p);
			}
		} finally {
			if (rs!=null) rs.close();
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
		return ret;
	}

	@Override
	public User write(User u) throws SQLException {

		User exists = readByDeviceId(u.getDeviceId());

		Connection c = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();

			/* If the User exists, we must:
			 * 1. If there's a lat and long in the User passed in, update the lat, long and time of their last position report, otherwise do nothing.
			 * 2. If their MSISDN IdentityHash has changed,
			 * 2a. Either look to see if it's changed to something in our database, and get the ID of that something, or
			 * 2b. If it's completely new, add it and get its ID, then
			 * 2c. Update the hashId of their user record to point to this new IdentityHash
			 * 
			 * It's meaningless to update their deviceId, as it's the way we found them.
			 */
			
			if (exists!=null) {
				if (!u.getLastPosition().equals(exists.getLastPosition())) {
					pst = c.prepareStatement(USER_UPDATE_POSITION_SQL);
					pst.setDouble(1, u.getLastPosition().getLatitude());
					pst.setDouble(2, u.getLastPosition().getLongitude());
					pst.setTimestamp(3, new Timestamp(u.getLastPosition().getWhen().getTime()));
					pst.setInt(4, exists.getId());
					pst.executeUpdate();
					pst.close();
				}
				
				if (!u.getMsisdnHash().equals(exists.getMsisdnHash())) {
					
					/* update the ID hash to this new value */
					int idHash = findOrCreateIdHash(c, u.getMsisdnHash());
					pst = c.prepareStatement(USER_UPDATE_IDHASH_SQL);
					pst.setInt(1, idHash);
					pst.setInt(2, exists.getId());
					pst.executeUpdate();
					pst.close();
					
				}
				
				u.setId(exists.getId());
			} else {
				
				/* Otherwise, this is a new user, so
				 * 
				 * 1. Add their idHash if necessary, getting its ID
				 * 2. Add a User record for them
				 * 3. Get the ID of this User record, and update the User passed in with it
				 */
				
				int idHash = findOrCreateIdHash(c, u.getMsisdnHash());
				pst = c.prepareStatement(USER_INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, u.getDeviceId());
				pst.setInt(2, idHash);
				if (u.getLastPosition()!=null) {
					pst.setDouble(3, u.getLastPosition().getLatitude());
					pst.setDouble(4, u.getLastPosition().getLongitude());
					pst.setTimestamp(5, new Timestamp(u.getLastPosition().getWhen().getTime()));
				} else {
					pst.setNull(3, Types.DOUBLE);
					pst.setNull(4, Types.DOUBLE);
					pst.setNull(5, Types.TIMESTAMP);
				}
				pst.executeUpdate();

				/* If they've just been added to the database for the first time, it will
				 * have allocated them an ID number automatically. Fetch this and set
				 * the ID class variable to it
				 */
				rs = pst.getGeneratedKeys();
				if (rs.next()) u.setId(rs.getInt(1));
				
			}
			
		} finally {
			if (rs!=null) rs.close();
			if (pst!=null) pst.close();
			if (c!=null) c.close();
		}
	
		return u;
	}
	
	/**
	 * Give an idHash and a Connection, finds the hash in the database and returns its ID,
	 * or if it isn't there, adds it and returns its ID.
	 * 
	 * @param c
	 * @param hash
	 * @return
	 * @throws SQLException
	 */
	
	private int findOrCreateIdHash(Connection c, String hash) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = c.prepareStatement(IDHASH_FIND_SQL);
			pst.setString(1, hash);
			rs = pst.executeQuery();
			if (rs.next()) return rs.getInt(1);
			
			rs.close();
			pst.close();
			
			pst = c.prepareStatement(IDHASH_INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, hash);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if (rs.next()) return rs.getInt(1);
		} finally {
			if (rs!=null) rs.close();
			if (pst!=null) pst.close();
		}
		
		// It should be impossible to reach this point
		throw new RuntimeException("couldn't add an IdentityHash");
	}

	@Override
	public boolean setAddressBook(int id, List<AddressBookEntry> book) {
		// if the user exists
		//   delete their old address book (but not hashes)
		//   add the new address book (and any hashes)
		//   return true
		// else return false
		// TODO Auto-generated method stub
		return false;
	}

}
