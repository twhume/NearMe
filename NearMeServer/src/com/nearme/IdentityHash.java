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
	private String hash;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	
}
