package com.nearme;

import static org.junit.Assert.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

public class UserFinderTest {
	
	private UserFinder uf = null;
	
	@Before
	public void setUp() {
		uf = new DummyUserFinder();
	}

	@Test
	public void testReadUser() {
		User testUser = new User(1, "android-123456", "hash-1234567890", new Position(-123.45, 67.89));

		assertEquals(testUser, uf.read(1));
		
		assertEquals(testUser, uf.readByHash("hash-1234567890"));
		assertNull(uf.readByHash("non-existent-hash"));
		assertNull(uf.readByHash("android-123456")); 

		assertEquals(testUser, uf.readByDeviceId("android-123456"));
		assertNull(uf.readByDeviceId("hash-1234567890"));
		assertNull(uf.readByDeviceId("non-existent-id"));
	}

	@Test
	public void testReadAddressBookNonExistentUser() {
		List<AddressBookEntry> book = uf.getAddressBook(12345); // non-existent user
		assertNull(book);
	}

	@Test
	public void testReadAddressBookEmpty() {
		List<AddressBookEntry> book = uf.getAddressBook(2); // existing user, no book
		assertEquals(0, book.size());
	}

	@Test
	public void testReadAddressBookFull() {
		User u = uf.read(1);
		List<AddressBookEntry> book = uf.getAddressBook(1); // existing user, with book
		assertEquals(3, book.size());
		assertEquals(new AddressBookEntry(1, u, "Tom", AddressBookEntry.PERM_HIDDEN, null), book.get(0));
		assertEquals(new AddressBookEntry(2, u, "Dick", AddressBookEntry.PERM_HIDDEN, null), book.get(1));
		assertEquals(new AddressBookEntry(3, u, "Harry", AddressBookEntry.PERM_HIDDEN, null), book.get(2));
	}

}
