package com.dailystudio.memory.where.hotspot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.geonames.WebService;

import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.person.PersonFeatures;
import com.dailystudio.memory.person.PersonInformation;
import com.dailystudio.memory.person.Persons;
import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.databaseobject.HotspotDatabaseModal;

public class IdentifiedResidenceChecker extends PeroidicalAsyncChecker {
	
	private final static String GEO_NAMES_USERNAME = "dailystudio";
	private final static long GEO_NAMES_TIMEOUT = (20 * CalendarUtils.SECOND_IN_MILLIS);
	
	private final static long LOOKUP_TIME_SPAN = (CalendarUtils.DAY_IN_MILLIS * 90);
	private final static long LOOKUP_TIME_SUB_SPAN = (CalendarUtils.DAY_IN_MILLIS * 30);
	
	private static final int HOTSPOT_PRE_CANDIDATE_LIMIT = 500;
	private static final int HOTSPOT_CANDIDATE_LIMIT = 50;
	
	private class HotspotCountry {
		String code;
		long duration;
		
		private HotspotCountry(String code, long duration) {
			this.code = code;
			this.duration = duration;
		}
		
		@Override
		public String toString() {
			return String.format("%s(0x%08x): %s, duration: %d[%s]",
					getClass().getSimpleName(),
					hashCode(),
					code,
					duration,
					CalendarUtils.durationToReadableString(duration));
		}
		
	}
	
	private class HotspotCountryComparator implements Comparator<HotspotCountry> {

		@Override
		public int compare(HotspotCountry lhs, HotspotCountry rhs) {
			if (lhs == null) {
				return -1;
			} else if (rhs == null) {
				return 1;
			}
			
			if (lhs.duration > rhs.duration) {
				return -1;
			} else if (lhs.duration < rhs.duration) {
				return 1;
			}
			
			return 0;
		}
		
	}
	
	private class TopCountry {
		String code;
		int count;
		
		private TopCountry(String code, int count) {
			this.code = code;
			this.count = count;
		}
		
		@Override
		public String toString() {
			return String.format("%s(0x%08x): %s, count: %d",
					getClass().getSimpleName(),
					hashCode(),
					code,
					count);
		}
		
	}
	
	private class TopCountryComparator implements Comparator<TopCountry> {

		@Override
		public int compare(TopCountry ltcc, TopCountry rtcc) {
			if (ltcc == null) {
				return -1;
			} else if (rtcc == null) {
				return 1;
			}
			
			if (ltcc.count > rtcc.count) {
				return -1;
			} else if (ltcc.count < rtcc.count) {
				return 1;
			}
			
			return 0;
		}
		
	}
	
	private Map<String, Long> mHotspotDurations = new HashMap<String, Long>();
	private Map<String, Integer> mTopCountryCounts = new HashMap<String, Integer>();

    public IdentifiedResidenceChecker(Context context) {
        super(context);
    }

    @Override
    public long getCheckInterval() {
        return CalendarUtils.DAY_IN_MILLIS;
    }

    @Override
    protected void doCheck(long now, long lastTimestamp) {
    	final String code = findFinalTopCountry();
    	if (TextUtils.isEmpty(code)) {
    		return;
    	}
		
		Locale l = new Locale("", code);
		String country = l.getDisplayCountry();
		
		Logger.debug("topCountry: code = %s, name = %s", 
				code,
				country);
		
		if (!TextUtils.isEmpty(code)) {
			final PersonInformation pi = new PersonInformation(mContext, 
					Persons.PERSON_ME);
			
			pi.setFeature(PersonFeatures.FEATURE_RESIDENCE, 
					code);
		}
    }
    
    private String findFinalTopCountry() {
    	final long end = System.currentTimeMillis();
    	final long start = end - LOOKUP_TIME_SPAN;
        
    	mTopCountryCounts.clear();
   	
    	long time = 0;
    	String topCountry = null;
    	int oldCount = 0;
    	for (time = start; time < end; time += LOOKUP_TIME_SUB_SPAN) {
    		topCountry = findTopCountry(time, time + LOOKUP_TIME_SUB_SPAN);
        	
        	if (mTopCountryCounts.containsKey(topCountry)) {
        		oldCount = mTopCountryCounts.get(topCountry);
        	} else {
        		oldCount = 0;
        	}
        	
        	mTopCountryCounts.put(topCountry, oldCount + 1);
    	}
    	
        List<TopCountry> topCountries = null;
        if (mTopCountryCounts.size() > 0) {
        	topCountries = new ArrayList<TopCountry>();
        	
        	for (Entry<String, Integer> entry: mTopCountryCounts.entrySet()) {
        		topCountries.add(new TopCountry(entry.getKey(), entry.getValue()));
        	}
        	
            Collections.sort(topCountries, new TopCountryComparator());
        }

        String code = null;
        if (topCountries != null && topCountries.size() > 0) {
        	code = topCountries.get(0).code;
        }
        
    	Logger.debug("topCountries: %s, final top: %s",
    			topCountries, 
    			code);
    	
    	return code;
    }
    
    private String findTopCountry(long start, long end) {
        final List<Hotspot> hpCandidates = 
                HotspotDatabaseModal.getHotspotCandidates(mContext, start, end, 
                		HOTSPOT_PRE_CANDIDATE_LIMIT);
        if (hpCandidates == null || hpCandidates.size() <= 0) {
            return null;
        }
        
        final HotspotFinder finder = new HotspotFinder();
        final List<Hotspot> hotspots = 
                finder.findHotspot(hpCandidates, HOTSPOT_CANDIDATE_LIMIT);
        if (hotspots == null || hotspots.size() <= 0) {
            return null;
        }
        
        mHotspotDurations.clear();
        
    	String country = null;
    	long oldDuration = 0;
        for (Hotspot hp: hotspots) {
        	country = getCountryOf(mContext,
        			hp.getLatitude(),
        			hp.getLongitude());
        	
        	if (TextUtils.isEmpty(country)) {
        		Logger.warnning("we got unreolved country code of hp [%f, %f], abort.", 
        				hp.getLatitude(),
        				hp.getLongitude());
        		continue;
        	}
        	
        	if (mHotspotDurations.containsKey(country)) {
        		oldDuration = mHotspotDurations.get(country);
        	} else {
        		oldDuration = 0;
        	}
        	
        	mHotspotDurations.put(country, oldDuration + hp.getDuration());
        }
        
        List<HotspotCountry> countries = null;
        if (mHotspotDurations.size() > 0) {
        	countries = new ArrayList<HotspotCountry>();
        	
        	for (Entry<String, Long> entry: mHotspotDurations.entrySet()) {
        		countries.add(new HotspotCountry(entry.getKey(), entry.getValue()));
        	}
        	
            Collections.sort(countries, new HotspotCountryComparator());
        }
        
        String topCountry = null;
        if (countries != null && countries.size() > 0) {
        	topCountry = countries.get(0).code;
        }
        
/*    	Logger.debug("countries: %s, [start: %s, end: %s], top: %s",
    			countries, 
    			CalendarUtils.timeToReadableString(start),
    			CalendarUtils.timeToReadableString(end),
    			topCountry);
*/    	
    	return topCountry;
    }
    
	public String getCountryOf(Context context, double lat, double lon) {
		String code = null/*GeoPointUtils.getCountryOf(context, lat, lon)*/;
		if (TextUtils.isEmpty(code)) {
			code = getCountryOfFromGeoNames(context, lat, lon);
		}
		
		return code;
	}
	
	public String getCountryOfFromGeoNames(Context context, double lat, double lon) {
		WebService.setUserName(GEO_NAMES_USERNAME);
		WebService.setConnectTimeOut((int)GEO_NAMES_TIMEOUT);
		
		String code = null;
		try {
			code = WebService.countryCode(lat, lon);
		} catch (IOException e) {
			Logger.warnning("query country code for [lat: %f, lon: %f] failure: %s",
					lat, lon, e.toString());
			
			code = null;	
		}
		
		return code;
	}
	
}
