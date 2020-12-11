package com.dailystudio.memory.where.hotspot;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.Hotspot;

public class HotspotFinder {
    
    public List<Hotspot> findHotspot(List<Hotspot> hpCandidates) {
        return findHotspot(hpCandidates, Constants.HOTSPOT_CANDIDATE_LIMIT);
    }
    
    public List<Hotspot> findHotspot(List<Hotspot> hpCandidates, int limit) {
        final List<Hotspot> candidates = hpCandidates;
        if (candidates == null) {
            return null;
        }
        
        final int N = candidates.size();
        if (N <= 0) {
            return null;
        }
        
        List<Hotspot> hotspots = new ArrayList<Hotspot>();
        
        Hotspot candidate;
        Hotspot nearBySpot;
        for (int i = 0; i < N; i++) {
            candidate = candidates.get(i);
            nearBySpot = (Hotspot)BaseLocationObject.findNearByLocation(
                    hotspots, candidate, Constants.IDSPOT_NEARY_BY_THRESHOLD);
//            Logger.debug("candidate: [%s], nearBySpot: [%s]", 
//                  candidate, nearBySpot);
            if (nearBySpot == null) {
                if (hotspots.size() >= limit) {
                    continue;
                }
                
                hotspots.add(candidate);
            } else {
                final long newDuration = 
                        nearBySpot.getDuration() + candidate.getDuration();
                final int newOccurence = 
                        nearBySpot.getOccurrence() + candidate.getOccurrence();
                
                nearBySpot.concatStartTimes(candidate.getRawStartTimes());
                nearBySpot.concatEndTimes(candidate.getRawEndTimes());
                nearBySpot.setDuration(newDuration);
                nearBySpot.setOccurrence(newOccurence);
            }
        }
        
        if (hotspots.size() <= 0) {
        	return hotspots;
        }
        
        List<Hotspot> filteredHotspots = new ArrayList<Hotspot>();
        for (Hotspot hotspot: hotspots) {
        	if (hotspot.getDuration() < Constants.HOTSPOT_DURATION_THRESHOLD) {
                Logger.debug("FILTER SHORT DURATION [ < %s ]: hotspot = %s ", 
                		CalendarUtils.durationToReadableString(Constants.HOTSPOT_DURATION_THRESHOLD),
               			hotspot);
                continue;
        	}
        	
    		filteredHotspots.add(hotspot);
        }
        
        return filteredHotspots;
    }

}
