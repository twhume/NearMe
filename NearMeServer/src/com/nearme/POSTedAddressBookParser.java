package com.nearme;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

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
	
	static Logger logger = Logger.getLogger(POSTedAddressBookParser.class);

	/**
	 * Parse a User and their address book from the JSON structure passed in.
	 * 
	 * @param input
	 */
	
	public void parse(String input) {
		
		logger.debug("parse() received " + input);

		/* Parse them from the JSON we received */
		
		Gson gson = new Gson();
		PostedUser postedUser = gson.fromJson(input, PostedUser.class);

		/* Now marshall this intermediate format - PostedUser - into a proper User and AddressBook */
		
		user = new User();
		user.setDeviceId(postedUser.deviceId);
		user.setMsisdnHash(postedUser.msisdnHash);
		
		book = new ArrayList<AddressBookEntry>();
		for (Entry e: postedUser.entries) {
			logger.debug("parse() got " + e);
			AddressBookEntry abe = new AddressBookEntry();
			abe.setName(e.name);
			abe.setOwner(user);
			abe.setPermission(e.permission);
			List<IdentityHash> hashes = new ArrayList<IdentityHash>();
			if (e.hashes!=null) {
				for (String s: e.hashes) {
					hashes.add(new IdentityHash(s));
				}
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
		int permission;
		
		public String toString() {
			return "name="+name+",hashes="+hashes;
		}
	}

	public User getUser() {
		return user;
	}

	public List<AddressBookEntry> getBook() {
		return book;
	}
	
}
