package com.nearme;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * The AddressBookServlet has a User sent into it using an HTTP POST. This class takes responsibility
 * for handling a JSON data structure and returning the User resulting from it, ready to be stored
 * in the database.
 * 
 * @author twhume
 *
 */

public class POSTedAddressBookParser {
	private User user = null;
	private List<AddressBookEntry> book = null;

	/**
	 * Parse a User and their address book from the JSON structure passed in.
	 * 
	 * @param input
	 */
	
	public void parse(String input) {

		/* Parse them from the JSON we received */
		
		Gson gson = new Gson();
		PostedUser postedUser = gson.fromJson(input, PostedUser.class);

		/* Now marshall this intermediate format - PostedUser - into a proper User and AddressBook */
		
		user = new User();
		user.setDeviceId(postedUser.deviceId);
		user.setMsisdnHash(postedUser.msisdnHash);
		
		book = new ArrayList<AddressBookEntry>();
		for (Entry e: postedUser.entries) {
			AddressBookEntry abe = new AddressBookEntry();
			abe.setName(e.name);
			abe.setOwner(user);
			
			List<IdentityHash> hashes = new ArrayList<IdentityHash>();
			for (String s: e.hashes) {
				hashes.add(new IdentityHash(s));
			}
			abe.setHashes(hashes);
			book.add(abe);
		}
	}
	
	/**
	 * Private classes corresponding to the data format of the JSON posted in.
	 * We marshall these into the local data-store classes...
	 * 
	 * @author twhume
	 *
	 */
	class PostedUser {
		String deviceId;
		String msisdnHash;
		List<Entry> entries;
	}

	class Entry {
		List<String> hashes;
		String name;
	}

	public User getUser() {
		return user;
	}

	public List<AddressBookEntry> getBook() {
		return book;
	}
	
}
