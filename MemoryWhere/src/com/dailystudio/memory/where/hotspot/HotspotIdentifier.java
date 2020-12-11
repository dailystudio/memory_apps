package com.dailystudio.memory.where.hotspot;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.datetime.DaysFilter;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspotDatabaseModal;

public class HotspotIdentifier {
    
    private static final float MATCH_IDENTITY_THRESHOLD = .6f;
    
    public static class HotspotIdentification {

        HotspotIdentityInfo hiInfo;
        float similarity = 0.f;
        
    }
    
    public static final HotspotIdentityInfo HOME = new HotspotIdentityInfo(
            HotspotIdentity.HOTSPOT_HOME,
            new int[] {
                1, 1, 1, 1, 
                1, 1, 1, 1,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 1, 1,
                1, 1, 1, 1,
            },
            null,
            R.string.hotspot_home, R.drawable.ic_idspot_home);

    public static final HotspotIdentityInfo WORKPLACE = new HotspotIdentityInfo(
            HotspotIdentity.HOTSPOT_WORKPLACE,
            new int[] {
                0, 0, 0, 0, 
                0, 0, 0, 0,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 0, 0, 0,
                0, 0, 0, 0,
            },
            DaysFilter.FILTER_WEEKDAYS,
            R.string.hotspot_workplace, R.drawable.ic_idspot_workplace);

    public static final HotspotIdentityInfo CASUAL_PLACE = new HotspotIdentityInfo(
            HotspotIdentity.HOTSPOT_CASUAL_PLACE,
            new int[] {
                0, 0, 0, 0, 
                0, 0, 0, 0,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                0, 0, 0, 0,
            },
            DaysFilter.FILTER_WEEKENDS,
            R.string.hotspot_casual_place, R.drawable.ic_idspot_coffee);

    private final static HotspotIdentityInfo[] KNOWN_IDENTITIES = {
        HOME,
        WORKPLACE,
//        CASUAL_PLACE,
    };
    
    public static HotspotIdentification findMatchedIdentity(Hotspot hotspot) {
        if (hotspot == null) {
            return null;
        }
        
        float similarity = 0.f;
        
        HotspotIdentification identification = null;
        for (HotspotIdentityInfo hid: KNOWN_IDENTITIES) {
            similarity = hid.getSimilarity(hotspot);
            if (similarity < MATCH_IDENTITY_THRESHOLD) {
                continue;
            }
            
            if (identification == null) {
                identification = new HotspotIdentification();
            }
            
            if (similarity > identification.similarity) {
                identification.similarity = similarity;
                identification.hiInfo = hid;
            }
        }
        
        return identification;
    }

    public static void identifyAndSaveHotspots(Context context, List<Hotspot> hotspots) {
        if (context == null || hotspots == null) {
            return;
        }
        
        final List<IdentifiedHotspot> oldIdspots = 
                IdentifiedHotspotDatabaseModal.getIdentifiedHotspots(context);
        
        List<IdentifiedHotspot> newIdspots = new ArrayList<IdentifiedHotspot>();
        
        BaseLocationObject similaryIdspot;
        IdentifiedHotspot idspot;
        for (Hotspot hp: hotspots) {
            idspot = identifyHotspot(context, hp);
            if (idspot == null) {
            	continue;
            }
            
            similaryIdspot = BaseLocationObject.findNearByLocation(
            		oldIdspots, idspot, Constants.IDSPOT_NEARY_BY_THRESHOLD);
            if (similaryIdspot == null) {
            	IdentifiedHotspotDatabaseModal.addIdentifiedHotspot(
            			context, idspot);
            } else {
                idspot.setId(similaryIdspot.getId());
                idspot.setLatitude(similaryIdspot.getLatitude());
                idspot.setLongitude(similaryIdspot.getLongitude());
                idspot.setAltitude(similaryIdspot.getAltitude());
                idspot.setTime(similaryIdspot.getTime());
                
                IdentifiedHotspotDatabaseModal.updateIdentifiedHotspot(
            			context, idspot);
                Logger.warnning("update existed identified idspot[%s]: newIdspot = %s", 
                		similaryIdspot,
                		idspot);
            }
            
            newIdspots.add(idspot);
        }
        
        List<IdentifiedHotspot> uselessIdspots = new ArrayList<IdentifiedHotspot>();
        if (oldIdspots != null) {
	        for (IdentifiedHotspot oldSpot: oldIdspots) {
	        	if (!newIdspots.contains(oldSpot)) {
	        		uselessIdspots.add(oldSpot);
	        	}
	        }
        }
        
        Logger.debug("uselessIdspots = %s", uselessIdspots);
        for (IdentifiedHotspot uspot: uselessIdspots) {
        	IdentifiedHotspotDatabaseModal.deleteIdentifiedHotspot(
        			context, uspot);
        }
    }
    
    private static IdentifiedHotspot identifyHotspot(Context context, Hotspot hotspot) {
        if (context == null || hotspot == null) {
            return null;
        }
        
        HotspotIdentification identification = 
                findMatchedIdentity(hotspot);
        if (identification == null) {
            return null;
        }
        
        final HotspotIdentityInfo hiInfo = identification.hiInfo;
        final float similarity = identification.similarity;
        
        IdentifiedHotspot idspot = 
                IdentifiedHotspotDatabaseModal.copyFromHotspot(
                        context, hotspot);
        
        idspot.setIdentity(hiInfo.identity);
        idspot.setSimilarity(similarity);
        
        return idspot;
    }

    public static HotspotIdentityInfo getIdentityInfo(HotspotIdentity identity) {
        if (identity == null) {
            return null;
        }
        
        for (HotspotIdentityInfo info: KNOWN_IDENTITIES) {
            if (info.identity == identity) {
                return info;
            }
        }

        return null;
    }
    
}
