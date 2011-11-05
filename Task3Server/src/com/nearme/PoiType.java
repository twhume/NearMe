package com.nearme;

/**
 * Class encapsulating a type of Point of Interest, e.g. "restaurants"
 * 
 * @author twhume
 *
 */
public class PoiType {
	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
