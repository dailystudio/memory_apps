package com.mapabc.minimap.map.vmap;

import java.util.HashMap;

import com.dailystudio.development.Logger;

public class NativeMap {

    static {
        System.loadLibrary("nativemaptranslator");
    }

    private static HashMap<String, double[]> sTransMap = 
    		new HashMap<String, double[]>();
    
    public NativeMap() {
    }

    public static double[] translate(double lat, double lon) {
        double trans[] = null;
        String mapKey = null;

        StringBuilder builder = new StringBuilder(String.valueOf(lat));
        if (builder != null) {
        	builder.append(String.valueOf(lon));
        	
        	mapKey = builder.toString();
        }
        
        if (mapKey != null && sTransMap.containsKey(mapKey)) {
        	trans = sTransMap.get(mapKey);
        }
        
        if (trans != null) {
    		Logger.debug("CACHED: [lat: %f, lon: %f] -> [lat: %f, lon: %f]",
    				lat, lon,
    				trans[0], trans[1]);
    		
        	return trans;
        }
        
        GeoPoint geopoint = new GeoPoint();
        
        nativeTranslatePointLocal(
        		(int)(lon * 1000000D), 
        		(int)(lat * 1000000D), 
        		geopoint);

        trans = new double[2];
        trans[0] = (double)geopoint.y / 1000000D;
        trans[1] = (double)geopoint.x / 1000000D;
        
		Logger.debug("TRANS: [lat: %f, lon: %f] -> [lat: %f, lon: %f]",
				lat, lon,
				trans[0], trans[1]);
        
        return trans;
    }
    
    public static double[] untranslate(double lat, double lon) {
        double trans[] = null;
        
        GeoPoint geopoint = new GeoPoint();
        
        nativeTranslatePointLocal(
        		(int)(1000000D * lon), 
        		(int)(1000000D * lat), 
        		geopoint);
        
        double transY = (double)geopoint.y / 1000000D;
        double transX = (double)geopoint.x / 1000000D;
        
        trans = new double[2];
        trans[0] = lat + (lat - transY);
        trans[1] = lon + (lon - transX); 
    
        return trans;
    }

    private static native int nativeTranslatePointLocal(int i, int j, GeoPoint geopoint);

}
