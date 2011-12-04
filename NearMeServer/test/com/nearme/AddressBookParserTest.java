package com.nearme;

import static org.junit.Assert.*;

import org.junit.Test;

public class AddressBookParserTest {

	@Test
	public void test() {
		String input = Util.convertStreamToString(getClass().getResourceAsStream("/test-address.json"));
		POSTedAddressBookParser ape = new POSTedAddressBookParser();
				
		ape.parse(input);
		assertEquals("e737c42a4a535833", ape.getUser().getDeviceId());
		assertEquals("bHqYH/Vf6/gwB+xTgrp9Mr7DyNg=\n", ape.getUser().getMsisdnHash());
		assertEquals(867, ape.getBook().size());
		
		assertEquals(AddressBookEntry.PERM_SHOWN, ape.getBook().get(0).getPermission());
		assertEquals(AddressBookEntry.PERM_HIDDEN, ape.getBook().get(1).getPermission());
		
	}

}
