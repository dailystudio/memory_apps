package com.dailystudio.memory.where.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dailystudio.app.utils.DeviceInfoUtils;
import com.dailystudio.development.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.mapabc.minimap.map.vmap.NativeMap;

public class GeoPointUtils {

    public static String getRegisteredNetwork(Context context) {
    	if (context == null) {
    		return null;
    	}
    	
    	TelephonyManager telmgr = 
    			(TelephonyManager) context.getSystemService(
    					Context.TELEPHONY_SERVICE);
    	if (telmgr == null) {
    		return null;
    	}

    	String countryCode = null;
    	/*
    	 * XXX: result of getNetworkCountryIso() is unreliable on CDMA
    	 * 		network or user is not register to a valid network.
    	 * 		During this situation, we use MCC for fallback;
    	 */
    	if (telmgr.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
    		countryCode = telmgr.getNetworkCountryIso();
    	} else {
    		countryCode = telmgr.getSimCountryIso();
    	}
    	
    	return countryCode;
    }
    
    public static boolean isInChina(Context context) {
    	String countryCode = getRegisteredNetwork(context);
    	Logger.debug("registered network = %s", countryCode);
    	if ("cn".equals(countryCode)) {
    		return true;
    	}
    	
    	return false;
    }

    public static boolean isInChina(Context context, double lat, double lon) {
    	String countryCode = getCountryOf(context, lat, lon);
    	if ("CN".equals(countryCode)) {
    		return true;
    	} else if (countryCode == null) {
    		return isInChina(context);
    	}
    	
    	return false;
    }

	private static double[] tranlateLatAndLon(Context context, 
			double lat, double lon) {
		final double origLat = lat;
		final double origLon = lon;
		
		double translated[] = null;
		
		if (true
				|| context == null
				|| "google_sdk".equals(Build.MODEL)
				|| !isInChina(context, lat, lon)) {
			translated = new double[2];
			
			translated[0] = origLat;
			translated[1] = origLon;
		} else {
			translated = NativeMap.translate(origLat, origLon);
		}
		
		return translated;
	}
	
	public static LatLng convertToLatLng(Context context, 
			double lat, double lon) {
		double translated[] = tranlateLatAndLon(context, lat, lon);
		
		final double transLat = translated[0];
		final double transLon = translated[1];

		LatLng latlng = new LatLng(transLat, transLon);

		return latlng;
	}

/*
	public static GeoPoint convertToGeoPoint(Context context,
			double lat, double lon) {
		double translated[] = tranlateLatAndLon(context, lat, lon);
		
		final double transLat = translated[0];
		final double transLon = translated[1];

		GeoPoint p = new GeoPoint(
				(int) (transLat * 1E6), 
				(int) (transLon * 1E6));

		return p;
	}
*/

/*
	public static String getAddressOfGeoPoint(Context context, LatLng latlng,
			String addressSepartor) {
		if (context == null || latlng == null) {
			return null;
		}
		
		GeoPoint p = new GeoPoint((int)(latlng.latitude * 1E6),
				(int)(latlng.longitude * 1E6));
		
		return getAddressOfGeoPoint(context, p, addressSepartor);
	}
*/

    public static String getAddressOfLatLng(Context context, LatLng latlng, String addressSepartor) {
        if (context == null || latlng == null) {
            return null;
        }

        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

        StringBuilder builder = new StringBuilder();
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude, 1);

            for (Address address: addresses) {
                Logger.debug("address(%s)", address);
            }
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    builder.append(addresses.get(0).getAddressLine(i));

                    if (!TextUtils.isEmpty(addressSepartor)) {
                        builder.append('\n');
                    }
                }
            }
        } catch (IOException e) {
            Logger.warnning("query address of LatLng(%f, %f) failed: %s",
                    latlng.latitude, latlng.longitude,
                    e.toString());
        }

        return builder.toString();
    }

/*
	public static String getAddressOfGeoPoint(Context context, GeoPoint p, String addressSepartor) {
		if (context == null || p == null) {
			return null;
		}
		
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

		StringBuilder builder = new StringBuilder();
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					p.getLatitudeE6()  / 1E6, 
					p.getLongitudeE6() / 1E6, 1);

			for (Address address: addresses) {
				Logger.debug("address(%s)", address);
			}
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
					builder.append(addresses.get(0).getAddressLine(i));
					
					if (!TextUtils.isEmpty(addressSepartor)) {
						builder.append('\n');
					}
				}
			}
		} catch (IOException e) {                
			Logger.warnning("query address of GeoPoint(%d, %d) failed: %s",
					p.getLatitudeE6(), p.getLongitudeE6(),
					e.toString());
		}   
		
		return builder.toString();
	}
*/

	public static String getCountryOf(Context context, double lat, double lon) {
		if (context == null) {
			return null;
		}
		
		String countryCode = null;
		
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());

		try {
			List<Address> addresses = geoCoder.getFromLocation(
					lat, lon, 1);

			if (addresses.size() > 0) {
				countryCode = addresses.get(0).getCountryCode();
			}
		} catch (IOException e) {                
			Logger.warnning("query country of point(%f, %f) failed: %s",
					lat, lon,
					e.toString());
			
			countryCode = null;
		}   
		
		Logger.debug("countryCode = [%s]", countryCode);

		return countryCode;
	}

}
