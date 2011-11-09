package com.nearme;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy implementation of the PoiFinder, used to spit out a few example Points of Interest
 * 
 * @author twhume
 *
 */

public class DummyPoiFinder implements PoiFinder {

	public List<Poi> find(PoiQuery pq) {
		ArrayList<Poi> ret = new ArrayList<Poi>();
		PoiType type = new PoiType("restaurant", 1);
		ret.add(new Poi("Fish'n'chips", -123.1, 456.0, type, 10));
		ret.add(new Poi("Kebabs", 12.6, -10, type, 11));
		ret.add(new Poi("Chinese", 12.5, 0.0, type, 12));
		return ret;
	}

}
