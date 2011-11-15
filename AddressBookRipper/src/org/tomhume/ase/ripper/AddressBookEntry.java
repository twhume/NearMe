package org.tomhume.ase.ripper;

import java.util.List;

public class AddressBookEntry {
	private String name;
	private List<String> hashes;
	
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
