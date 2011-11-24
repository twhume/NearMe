package org.tomhume.ase.ripper;

import java.util.List;

public class AddressBookEntry {
	private String name;
	private List<String> hashes;
	
	public static final int PERM_HIDDEN = 0; /* indicates user wishes to hide from this contact */
	public static final int PERM_SHOWN = 1; /* indicates user is happy to be seen by this contact */

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getHashes() {
		return hashes;
	}
	public void setHashes(List<String> hashes) {
		this.hashes = hashes;
	}

}
