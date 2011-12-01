package com.nearme;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for methods that read user data from a database
 * 
 * @author twhume
 *
 */

public interface UserDAO {

	/* Look up a user by ID number */
	public User read(int i) throws SQLException;
	
	/* Look up a user by their IdentityHash */
	public User readByHash(String string) throws SQLException;
	
	/* Look up a user by their device ID */
	public User readByDeviceId(String string) throws SQLException;
	
	/* Look up the address book for a given user */
	public List<AddressBookEntry> getAddressBook(int i) throws SQLException;
	
	/* Look up the nearest other Users for a given User */
	public List<Poi> getNearestUsers(User u, int radius) throws SQLException;

	/* Write the User out into storage */
	public User write(User u) throws SQLException;
	
	/* Set the AddressBook for the given User, removing old entries */
	public boolean setAddressBook(int id, List<AddressBookEntry> book) throws SQLException;
	
	/* Get a list of Hashes for the given user (identified by android device ID), that the user shares location with */
	public List<IdentityHash> getPermissions(User u) throws SQLException;

	/* Updates the address book for the user identified by deviceId, to withold location-sharing permissions
	 * from all users bar those passed in in the "perms" list
	 */
	public boolean setPermissions(User u, String[] perms) throws SQLException;

	/* Deletes the user from the system completely, so they are effectively unsubscribed */
	public void deleteUser(User u) throws SQLException;

}
