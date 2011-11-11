package com.nearme;

import java.sql.SQLException;
import java.util.List;

public interface UserFinder {

	/* Look up a user by ID number */
	public User read(int i) throws SQLException;
	
	/* Look up a user by their IdentityHash */
	public User readByHash(String string) throws SQLException;
	
	/* Look up a user by their device ID */
	public Object readByDeviceId(String string) throws SQLException;
	
	/* Look up the address book for a given user */
	public List<AddressBookEntry> getAddressBook(int i) throws SQLException;

}
