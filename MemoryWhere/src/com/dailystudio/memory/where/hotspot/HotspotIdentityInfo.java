package com.dailystudio.memory.where.hotspot;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.databaseobject.Hotspot;

public class HotspotIdentityInfo {

    public HotspotIdentity identity;
    public int[] sampleHourDistribs;
    public int[] weekdaysFilter;
    public int labelResId;
    public int iconResId;
    
    public HotspotIdentityInfo(HotspotIdentity hi,
            int[] sampleDistribs, int[] daysFilter, int label, int icon) {
        identity = hi;
        sampleHourDistribs = sampleDistribs;
        weekdaysFilter = daysFilter;
        labelResId = label;
        iconResId = icon;
    }
    
    public boolean isValid() {
        return (sampleHourDistribs != null && sampleHourDistribs.length == 24);
    }

    public float getSimilarity(Hotspot hotspot) {
        Logger.debug("hotspot = %s", hotspot);
        if (hotspot == null) {
            return 0.f;
        }
        
        final HotspotHourDistribCalculator hhdCal = 
                new HotspotHourDistribCalculator();
        
        final int[] hourDistribs =
                hhdCal.calculate(hotspot.getRawStartTimes(),
                        hotspot.getRawEndTimes(),weekdaysFilter );
        
        return getSimilarity(hourDistribs);
    }
    
    public float getSimilarity(int[] hourDistribs) {
        if (hourDistribs == null || hourDistribs.length != 24) {
            return 0.f;
        }
        
        if (!isValid()) {
            return 0.f;
        }
        
        final int hourDistribBits = 
                to24HoursBitPattern(hourDistribs);
        final int smapleDistribBits = 
                to24HoursBitPattern(sampleHourDistribs);
        final int matchedBits = (hourDistribBits & smapleDistribBits);
        
        Logger.debug("%s: [hourDistribBits = %s, smapleDistribBits = %s, matchedBits = %s]", 
        		identity,
                to32BitsString(hourDistribBits),
                to32BitsString(smapleDistribBits),
                to32BitsString(matchedBits));
        
        if (hourDistribBits <= 0) {
            return 0.f;
        }
        
        final int matchedCount = count1Bits(matchedBits);
        final int totalCount = count1Bits(hourDistribBits) + count1Bits(smapleDistribBits)
                - matchedCount;
        if (totalCount <= 0) {
            return 0.f;
        }
        
        Logger.debug("%s: [similarity = %f]", 
        		identity, (float)matchedCount / totalCount);

        return (float)matchedCount / totalCount;
    }
    
    private int count1Bits(int bits) {
        return Integer.bitCount(bits);
    }
    
    public static int to24HoursBitPattern(int[] array) {
        if (array == null || array.length != 24) {
            return 0;
        }

        int avg = calculateAverage(array);
        if (avg <= 0) {
            avg = 1;
        }
        
//        Logger.debug("avg = %d", avg);
        int bits = 0;
        for (int i = 0; i < 24; i++) {
            if (array[i] >= avg) {
                bits |= (0x1 << i);
            }
//            Logger.debug("[%d] = %d, bits = %s", i, array[i], bitsToString(bits));
        }
        
        return bits;
    }
    
    public static int[] to24HoursArray(int bits) {
        int[] array = new int[24];
        for (int i = 0; i < 24; i++) {
            array[i] = ((bits & (0x1 << i)) == (0x1 << i) ? 1: 0);
        }
        
        Logger.debug("array = %s", hourDistribsToString(array));
        return array;
    }
  
    private static int calculateAverage(int[] array) {
        if (array == null || array.length <= 0) {
            return 0;
        }
        
        int sum = 0;
        for (int i : array) {
            sum += i;
        }

        return Math.round(sum / (float)array.length);
    }
    
    public static String to32BitsString(int bits) {
        StringBuilder builder = new StringBuilder(
                Integer.toBinaryString(bits));
        
        final int len = builder.length();
        if (len < 32) {
            final int missed0len = 
                    (32 - len);
            
            for (int i = 0; i < missed0len; i++) {
                builder.insert(0, 0);
            }
        }
        
        return builder.toString();
    }
    
    public static String hourDistribsToString(int[] hourDistribs) {
        if (hourDistribs == null || hourDistribs.length <= 0) {
            return null;
        }
        
        StringBuilder builder = new StringBuilder("[ ");
        for (int i = 0 ; i < hourDistribs.length; i++) {
            builder.append(hourDistribs[i]);
            
            builder.append(' ');
        }
        
        builder.append(']');
        
        return builder.toString();
    }

}
