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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hashes == null) ? 0 : hashes.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + permission;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressBookEntry other = (AddressBookEntry) obj;
		if (hashes == null) {
			if (other.hashes != null)
				return false;
		} else if (!hashes.equals(other.hashes))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (permission != other.permission)
			return false;
		return true;
	}
}
