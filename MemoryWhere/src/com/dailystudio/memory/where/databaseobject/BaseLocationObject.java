package com.dailystudio.memory.where.databaseobject;

import java.util.Comparator;
import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.utils.GeoPointUtils;
import com.google.android.gms.maps.model.LatLng;

public class BaseLocationObject extends TimeCapsule {
    
    public static class BaseLocationComparator<T extends BaseLocationObject> 
        implements Comparator<T> {

        @Override
        public int compare(T loc1, T loc2) {
            if (loc1 == null) {
                return 1;
            } else if (loc2 == null) {
                return -1;
            }
            
            final double lat1 = loc1.getLatitude();
            final double lat2 = loc2.getLatitude();
            if (lat1 != lat2) {
                return (lat1 < lat2 ? -1 : 1);
            }
            
            final double lon1 = loc1.getLongitude();
            final double lon2 = loc2.getLongitude();
            if (lon1 != lon2) {
                return (lon1 < lon2 ? -1 : 1);
            }
            
            final double alt1 = loc1.getAltitude();
            final double alt2 = loc2.getAltitude();
            if (alt1 != alt2) {
                return (alt1 < alt2 ? -1 : 1);
            }
            
            return 0;
        }
        
    }

	public static final double COMMON_NEAR_BY_DISTANCE = 10.0;
	
	public static final Column COLUMN_LATITUDE = new DoubleColumn("latitude");
	public static final Column COLUMN_LONGITUDE = new DoubleColumn("longitude");
	public static final Column COLUMN_ALTITUDE = new DoubleColumn("altitude");
	
	private final static Column[] sCloumns = {
		COLUMN_LATITUDE,
		COLUMN_LONGITUDE,
		COLUMN_ALTITUDE,
	};

	public BaseLocationObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public void setLatitude(double latitude) {
		setValue(COLUMN_LATITUDE, latitude);
	}

	public double getLatitude() {
		return getDoubleValue(COLUMN_LATITUDE);
	}

	public void setLongitude(double longitude) {
		setValue(COLUMN_LONGITUDE, longitude);
	}

	public double getLongitude() {
		return getDoubleValue(COLUMN_LONGITUDE);
	}

	public void setAltitude(double altitude) {
		setValue(COLUMN_ALTITUDE, altitude);
	}

	public double getAltitude() {
		return getDoubleValue(COLUMN_ALTITUDE);
	}

	public boolean isNearBy(BaseLocationObject location) {
		return isNearBy(location, COMMON_NEAR_BY_DISTANCE);
	}

	public boolean isNearBy(BaseLocationObject location, double nearByDistance) {
		if (location == null) {
			return false;
		}
		
		final double distance = getDistanceBetween(this, location);
		Logger.debug("this[%s], location[%s], dis = %f (threshold = %f)",
				this, location,
				distance,
				nearByDistance);

		return (distance <= nearByDistance);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BaseLocationObject == false) {
			return false;
		}
		
		BaseLocationObject location = (BaseLocationObject)o;
		
		return (getLatitude() == location.getLatitude()
				&& getLongitude() == location.getLongitude()
				&& getAltitude() == getAltitude());
	}
	
	public LatLng convertToGeoPoint(Context context) {
		return GeoPointUtils.convertToLatLng(context,
				getLatitude(), getLongitude());
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): time(%s), loc(lat: %f, lon: %f, alt: %f)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				CalendarUtils.timeToReadableString(getTime()),
				getLatitude(),
				getLongitude(),
				getAltitude());
	}
	
	private final static double DEGREE_METERS = 111319.55;
	private final static double EARTH_RADIS = (6378.137 * 1000);
	private final static double PI = 3.1415926535898;
	
	public static double getDistanceBetween(double lat1, double lon1, double lat2, double lon2) {
		return getDistanceAlgorithm2(
				degreeToRadius(lat1), degreeToRadius(lon1), 
				degreeToRadius(lat2), degreeToRadius(lon2));
	}
	
	public static double getDistanceBetween(BaseLocationObject loc1, BaseLocationObject loc2) {
		return getDistanceAlgorithm2(loc1, loc2);
	}
	
	static double getDistanceAlgorithm1(BaseLocationObject loc1, BaseLocationObject loc2) {
		if (loc1 == null || loc2 == null) {
			return .0;
		}
		
		final double deltaLat = loc1.getLatitude() - loc2.getLatitude();
		final double deltaLon = loc1.getLongitude() - loc2.getLongitude();
		
		final double deltaLatM = deltaLat * DEGREE_METERS;
		final double deltaLonM = deltaLon * DEGREE_METERS * Math.cos(loc1.getLatitude() * PI / 180);
		
		return Math.sqrt((deltaLatM * deltaLatM) + (deltaLonM * deltaLonM));
	}
	
	static double getDistanceAlgorithm2(BaseLocationObject loc1, BaseLocationObject loc2) {
		if (loc1 == null || loc2 == null) {
			return .0;
		}
		
		final double lat1 = degreeToRadius(loc1.getLatitude());
		final double lon1 = degreeToRadius(loc1.getLongitude());
		final double lat2 = degreeToRadius(loc2.getLatitude());
		final double lon2 = degreeToRadius(loc2.getLongitude());
		
		return getDistanceAlgorithm2(lat1, lon1, lat2, lon2);
	}
	
	/*
	 * XXX: From Google Maps scripts
	 */
	static double getDistanceAlgorithm2(double lat1, double lon1, double lat2, double lon2) {
		final double pow1 = Math.pow(Math.sin((lat1 - lat2) / 2), 2);
		final double pow2 = Math.pow(Math.sin((lon1 - lon2) / 2), 2); 
		
		return (2 * Math.asin(Math.sqrt(pow1 + Math.cos(lat1) * Math.cos(lat2) * pow2)) * EARTH_RADIS);
	}
	
	private static double degreeToRadius(double degree) {
		return (degree * PI / 180);
	}
	
    public static BaseLocationObject findNearByLocation(
            List<? extends BaseLocationObject> locations,
            BaseLocationObject sampleLocation) {
    	return findNearByLocation(locations, sampleLocation, 
    			Constants.NEARY_BY_THRESHOLD);
    }
    
    public static BaseLocationObject findNearByLocation(
            List<? extends BaseLocationObject> locations,
            BaseLocationObject sampleLocation,
            long nearByDistance) {
        if (locations == null 
                || locations.size() <= 0
                || sampleLocation == null) {
            return null;
        }
        
        BaseLocationObject nearByLoc = null;      
        for (BaseLocationObject loc: locations) {
            if (loc.isNearBy(sampleLocation, nearByDistance)) {
                nearByLoc = loc;
                
                break;
            }
        }
        
        return nearByLoc;
    }

}
