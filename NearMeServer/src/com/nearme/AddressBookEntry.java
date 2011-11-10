package com.nearme;

import java.util.List;

/**
 * A single entry in the users address-book: a single friend, as it were.
 * 
 * Each User may have 0 or more AddressBookEntries.
 * Each AddressBookEntry must have 1 or more IdentityHashes.
 * 
 * @author twhume
 *
 */

public class AddressBookEntry {

	public static final int PERM_HIDDEN = 0;	/* indicates user wishes to hide from this contact */
	public static final int PERM_SHOWN = 1;	/* indicates user is happy to be seen by this contact */
	
	private int id;							/* ID of this entry, unique index in db */
	private User owner;						/* Owner of this entry */
	private String name;					/* Name in address-book of this entry */
	private int permission = PERM_HIDDEN;	/* Permission granted to this entry */
	private List<IdentityHash> hashes;		/* List of IdentityHashes associated with this AddressBookEntry */
	
	public AddressBookEntry(int i, User u, String n, int p, List<IdentityHash> h) {
		this.id = i;
		this.owner = u;
		this.name = n;
		this.permission = p;
		this.hashes = h;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	public List<IdentityHash> getHashes() {
		return hashes;
	}
	public void setHashes(List<IdentityHash> hashes) {
		this.hashes = hashes;
	}
}
