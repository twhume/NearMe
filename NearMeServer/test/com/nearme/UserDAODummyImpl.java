package com.nearme;

/**
 * For testing purposes: a sample UserFinder implementation that contains a single user
 * with an address-book of 3 friends.
 * 
 */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAODummyImpl implements UserDAO {
	private User testUserWithBook = null;
	private User testUserWithoutBook = null;
	private List<AddressBookEntry> addressBook = null;
	
	public UserDAODummyImpl() {
		testUserWithBook = new User(1, "android-123456", "hash-1234567890", new Position(-123.45, 67.89));
		testUserWithoutBook = new User(2, "android-7890", "hash-0987654321", new Position(-123.45, 67.89));
		addressBook = new ArrayList<AddressBookEntry>();
		addressBook.add(new AddressBookEntry(1, testUserWithBook, "Tom", AddressBookEntry.PERM_HIDDEN, null));
		addressBook.add(new AddressBookEntry(2, testUserWithBook, "Dick", AddressBookEntry.PERM_HIDDEN, null));
		addressBook.add(new AddressBookEntry(3, testUserWithBook, "Harry", AddressBookEntry.PERM_HIDDEN, null));
	}
	
	public User read(int i) {
		switch (i) {
			case 1: return testUserWithBook;
			case 2: return testUserWithoutBook;
			default: return null;
		}
	}

	public User readByHash(String string) {
		if (string.equals("hash-1234567890")) return testUserWithBook;
		else if (string.equals("hash-0987654321")) return testUserWithoutBook;
		return null;
	}

	public Object readByDeviceId(String string) {
		if (string.equals("android-123456")) return testUserWithBook;
		else if (string.equals("android-7890")) return testUserWithoutBook;
		return null;
	}

	@Override
	public List<AddressBookEntry> getAddressBook(int i) {
		switch (i) {
			case 1: return addressBook;
			case 2: return new ArrayList<AddressBookEntry>();
			default:	return null;
		}
	}

	@Override
	public List<Poi> getNearestUsers(User u, int radius) throws SQLException {
		ArrayList<Poi> ret = new ArrayList<Poi>();
		// Dummy method, we don't do much to test this
		return ret;
	}

	public boolean write(User u) throws SQLException {
		// Again, the dummy implementation isn't tested much here
		return false;
	}

	@Override
	public boolean setAddressBook(int id, List<AddressBookEntry> book) {
		return false;
	}
	
}
