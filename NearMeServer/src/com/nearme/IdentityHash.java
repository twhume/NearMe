package com.nearme;

/**
 * An IdentityHash is a phone number which has been run through SHA-1 or similar. We do this so that the
 * server can keep track of lots of phone numbers and use them to identify users uniquely, without actually
 * knowing what those phone numbers are. It's a privacy thang.
 * 
 * @author twhume
 *
 */

public class IdentityHash {
	private int id;
	private String hash;

	public IdentityHash(int i, String s) {
		this.id = i;
		this.hash = s;
	}
	
	public IdentityHash(String s) {
		this.hash = s;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + id;
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
		IdentityHash other = (IdentityHash) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))  {
			System.err.println("F " + hash + "!="+ other.hash);
			return false;
		} if (id != other.id)
			return false;
		return true;
	}
	
	
}
