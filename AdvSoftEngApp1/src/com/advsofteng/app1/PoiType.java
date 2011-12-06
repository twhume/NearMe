package com.advsofteng.app1;

/**
 * Class encapsulating a type of Point of Interest, e.g. "restaurants"
 * 
 * @author twhume
 * 
 */
public class PoiType {
	
	public static final int FRIEND = 0;
	
	private int id;
	private String name;

	public PoiType() {}
	
	public PoiType(String n, int i) {
		this.name = n;
		this.id = i;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PoiType other = (PoiType) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
