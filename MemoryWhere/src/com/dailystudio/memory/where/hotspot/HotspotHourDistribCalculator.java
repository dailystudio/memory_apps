package com.dailystudio.memory.where.hotspot;

import java.util.List;

import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.utils.TimeRange;

public class HotspotHourDistribCalculator {
    
    public int[] calculate(String rawStartTimes, String rawEndTimes) {
    	return calculate(rawStartTimes, rawEndTimes, null);
    }
    
    public int[] calculate(String rawStartTimes, String rawEndTimes,
    		int[] daysFilter) {
        if (rawStartTimes == null || rawEndTimes == null) {
            return null;
        }
        
        final List<Long> startTimes = Hotspot.convertTimes(rawStartTimes);
        final List<Long> endTimes = Hotspot.convertTimes(rawEndTimes);

        return calculate(startTimes, endTimes, daysFilter);
    }
    
    public int[] calculate(List<Long> startTimes, List<Long> endTimes,
    		int[] daysFilter) {
        if (startTimes == null || endTimes == null
                || startTimes.size() != endTimes.size()) {
            return null;
        }
        
        final int N = startTimes.size();
        
        int[] hourDistrib = new int[24];

        TimeRange tr = null;
        int[] distrib = null;
        for (int i = 0; i < N; i++) {
            tr = new TimeRange(startTimes.get(i), endTimes.get(i), daysFilter);
//            Logger.debug("tr = %s", tr);
            distrib = tr.calculateHourDistrib();
            
            sumDistrib(hourDistrib, distrib);
        }
        
        return hourDistrib;
    }

    private void sumDistrib(int[] sumDistrib, int[] distrib) {
        if (sumDistrib == null || sumDistrib.length < 24) {
            return;
        }
        
        if (distrib == null || distrib.length < 24) {
            return;
        }
        
        for (int i = 0; i < 24; i++) {
            sumDistrib[i] += distrib[i];
        }
    }
    
}
