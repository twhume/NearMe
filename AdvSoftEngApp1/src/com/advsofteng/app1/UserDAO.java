package com.advsofteng.app1;

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
	public Object readByDeviceId(String string) throws SQLException;

	/* Look up the address book for a given user */
	public List<AddressBookEntry> getAddressBook(int i) throws SQLException;

	/* Look up the nearest other Users for a given User */
	public List<Poi> getNearestUsers(User u, int radius) throws SQLException;

	/* Write the User out into storage */
	public User write(User u) throws SQLException;

	/* Set the AddressBook for the given User, removing old entries */
	public boolean setAddressBook(int id, List<AddressBookEntry> book) throws SQLException;
}

