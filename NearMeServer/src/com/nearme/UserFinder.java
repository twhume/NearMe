package com.nearme;

import java.util.List;

public interface UserFinder {

	/* Look up a user by ID number */
	public User read(int i);
	
	/* Look up a user by their IdentityHash */
	public User readByHash(String string);
	
	/* Look up a user by their device ID */
	public Object readByDeviceId(String string);
	
	/* Look up the address book for a given user */
	public List<AddressBookEntry> getAddressBook(int i);

}
