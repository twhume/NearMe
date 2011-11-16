package com.nearme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		return new User(rs.getInt("user.id"),
					rs.getString("user.deviceId"),
					rs.getString("idHash.hash"),
					new Position(rs.getDouble("user.latitude"), rs.getDouble("user.longitude"), new java.util.Date(rs.getTimestamp("user.lastReport").getTime()))
				);
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
	public Object readByDeviceId(String string) throws SQLException {
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

}
