package org.tomhume.ase.ripper;

import java.util.List;

public class AddressBook {
	private String deviceId;
	private String msisdnHash;
	private List<AddressBookEntry> entries;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOwnerHash() {
		return msisdnHash;
	}
	public void setOwnerHash(String ownerHash) {
		this.msisdnHash = ownerHash;
	}
	public List<AddressBookEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<AddressBookEntry> entries) {
		this.entries = entries;
	}
}
