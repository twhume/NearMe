package com.advsofteng.app1;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class NearMeApplication extends Application {
	private List<Poi> pois = new ArrayList<Poi>();

	public List<Poi> getPois() {
		return pois;
	}

	public void setPois(List<Poi> pois) {
		this.pois = pois;
	}
	
	
}
