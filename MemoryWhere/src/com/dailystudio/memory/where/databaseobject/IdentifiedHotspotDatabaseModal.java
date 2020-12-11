package com.dailystudio.memory.where.databaseobject;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.app.dataobject.DatabaseWriter;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.hotspot.HotspotHourDistribCalculator;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public class IdentifiedHotspotDatabaseModal {

    public static IdentifiedHotspot copyFromHotspot(Context context, Hotspot hotspot) {
        if (context == null || hotspot == null) {
            return null;
        }
        
        IdentifiedHotspot idspot = new IdentifiedHotspot(context);
        
        idspot.setLatitude(hotspot.getLatitude());
        idspot.setLongitude(hotspot.getLongitude());
        idspot.setAltitude(hotspot.getAltitude());
        idspot.setDuration(hotspot.getDuration());
        idspot.setOccurrence(hotspot.getOccurrence());
        idspot.setTime(System.currentTimeMillis());
        
        final HotspotHourDistribCalculator hhdCal = 
                new HotspotHourDistribCalculator();
        
        final int[] hourDistribs =
                hhdCal.calculate(hotspot.getRawStartTimes(),
                        hotspot.getRawEndTimes());

        final int distribBits = 
                HotspotIdentityInfo.to24HoursBitPattern(hourDistribs);
        
        idspot.setDistribBits(distribBits);
        
        return idspot;
    }
    
    public static void addIdentifiedHotspot(Context context,
            IdentifiedHotspot idspot) {
        if (context == null || idspot == null) {
            return;
        }
        
        final DatabaseWriter<IdentifiedHotspot> writer = 
                new DatabaseWriter<IdentifiedHotspot>(context, 
                        IdentifiedHotspot.class);
        
        writer.insert(idspot);
    }
    
    public static void updateIdentifiedHotspot(Context context,
            IdentifiedHotspot idspot) {
        if (context == null || idspot == null) {
            return;
        }
        
        final TimeCapsuleDatabaseWriter<IdentifiedHotspot> writer = 
                new TimeCapsuleDatabaseWriter<IdentifiedHotspot>(context,
                        IdentifiedHotspot.class);
        
        writer.update(idspot);
    }
    
    public static void deleteIdentifiedHotspot(Context context,
            IdentifiedHotspot idspot) {
        if (context == null || idspot == null) {
            return;
        }
        
        final TimeCapsuleDatabaseWriter<IdentifiedHotspot> idspotWriter = 
                new TimeCapsuleDatabaseWriter<IdentifiedHotspot>(context, 
                		IdentifiedHotspot.class);
        idspotWriter.delete(idspot);
        
        @SuppressWarnings("unused")
		final TimeCapsuleDatabaseWriter<OldIdentifiedHotspot> writer = 
                new TimeCapsuleDatabaseWriter<OldIdentifiedHotspot>(context, 
                		OldIdentifiedHotspot.class);
        
        OldIdentifiedHotspot oIdspot = new OldIdentifiedHotspot(context);
        
        oIdspot.setId(idspot.getId());
        oIdspot.setIdentity(idspot.getIdentity());
        oIdspot.setLatitude(idspot.getLatitude());
        oIdspot.setLongitude(idspot.getLongitude());
        oIdspot.setAltitude(idspot.getAltitude());
        oIdspot.setDistribBits(idspot.getDistribBits());
        oIdspot.setDuration(idspot.getDuration());
        oIdspot.setOccurrence(idspot.getOccurrence());
        oIdspot.setSimilarity(idspot.getSimilarity());
        oIdspot.setTime(idspot.getTime());
        oIdspot.setDeleteTime(System.currentTimeMillis());
        
//        writer.insert(oIdspot);
    }
    
	public static List<IdentifiedHotspot> getIdentifiedHotspots(Context context) {
		if (context == null) {
			return null;
		}
		
		final DatabaseReader<IdentifiedHotspot> reader = 
				new DatabaseReader<IdentifiedHotspot>(context, 
				        IdentifiedHotspot.class);

		Query query = new Query(IdentifiedHotspot.class);
		
		return reader.query(query);
	}
	
	public static List<OldIdentifiedHotspot> getOldIdentifiedHotspots(Context context) {
		if (context == null) {
			return null;
		}
		
		final DatabaseReader<OldIdentifiedHotspot> reader = 
				new DatabaseReader<OldIdentifiedHotspot>(context, 
						OldIdentifiedHotspot.class);

		Query query = new Query(OldIdentifiedHotspot.class);
		
		return reader.query(query);
	}
	
	public static IdentifiedHotspot getIdentifiedHotspot(Context context, 
			int idSpotId) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<IdentifiedHotspot> reader = 
				new TimeCapsuleDatabaseReader<IdentifiedHotspot>(context, 
				        IdentifiedHotspot.class);

		Query query = new Query(IdentifiedHotspot.class);
		
		ExpressionToken selToken = 
				IdentifiedHotspot.COLUMN_ID.eq(idSpotId);
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
	public static IdentifiedHotspot getOldIdentifiedHotspot(Context context, 
			int idSpotId) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<OldIdentifiedHotspot> reader = 
				new TimeCapsuleDatabaseReader<OldIdentifiedHotspot>(context,
						OldIdentifiedHotspot.class);

		Query query = new Query(OldIdentifiedHotspot.class);
		
		ExpressionToken selToken = 
				OldIdentifiedHotspot.COLUMN_ID.eq(idSpotId);
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
	public static IdentifiedHotspot getIdentifiedHotspot(Context context, 
			BaseLocationObject location) {
		if (context == null || location == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<IdentifiedHotspot> reader = 
				new TimeCapsuleDatabaseReader<IdentifiedHotspot>(context, 
				        IdentifiedHotspot.class);

		Query query = new Query(IdentifiedHotspot.class);
		
		ExpressionToken selToken = 
				IdentifiedHotspot.COLUMN_LATITUDE.eq(location.getLatitude())
					.and(IdentifiedHotspot.COLUMN_LONGITUDE.eq(location.getLongitude()))
					.and(IdentifiedHotspot.COLUMN_ALTITUDE.eq(location.getAltitude()));
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
	public static IdentifiedHotspot findMatchedIdentifiedHotspot(Context context, 
			BaseLocationObject loc) {
		if (context == null || loc == null) {
			return null;
		}
		
		final List<IdentifiedHotspot> idspots = getIdentifiedHotspots(context);
		if (idspots == null) {
			return null;
		}
		
		IdentifiedHotspot foundSpot = null;
		for (IdentifiedHotspot idspot: idspots) {
			if (idspot.isNearBy(loc, Constants.NEARY_BY_THRESHOLD)) {
				foundSpot = idspot;
				
				break;
			}
		}
		
		return foundSpot;
	}

}
