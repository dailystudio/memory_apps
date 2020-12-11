package com.mapabc.minimap.map.vmap;

import java.io.Serializable;

public class GeoPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9218456739473275122L;
	
	public int x;
    public int y;

    public GeoPoint() {
    }

    public GeoPoint(int i, int j) {
        x = i;
        y = j;
    }

}
