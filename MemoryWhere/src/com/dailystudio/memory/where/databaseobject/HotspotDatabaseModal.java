package com.dailystudio.memory.where.databaseobject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.memory.where.Constants;

public class HotspotDatabaseModal {

    public static List<Hotspot> getHotspotCandidates(Context context) {
    	final long end = System.currentTimeMillis();
    	final long start = end - Constants.HOTSPOT_PRE_CANDIDATE_TIME_SPAN;
    	
        return getHotspotCandidates(context, start, end, Constants.HOTSPOT_PRE_CANDIDATE_LIMIT);
    }
    
    public static List<Hotspot> getHotspotCandidates(Context context, long start, long end) {
        return getHotspotCandidates(context, start, end, Constants.HOTSPOT_PRE_CANDIDATE_LIMIT);
    }
    
	public static List<Hotspot> getHotspotCandidates(Context context, 
			long start, long end, int limit) {
		if (context == null) {
			return null;
		}
		
		if (start > end) {
			return null;
		}
		
		final DatabaseReader<MemoryLocation> reader = 
				new DatabaseReader<MemoryLocation>(context, 
				        MemoryLocation.class);

		TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(MemoryLocation.class);
			
		Query query = builer.getQuery(start, end);		

        OrderingToken groupByToken = 
                MemoryLocation.COLUMN_LATITUDE.groupBy()
                    .with(MemoryLocation.COLUMN_LONGITUDE.groupBy())
                    .with(MemoryLocation.COLUMN_ALTITUDE.groupBy());
        if (groupByToken == null) {
            return null;
        }
        
        OrderingToken orderBy =
                Hotspot.COLUMN_SUM_DURATION.orderByDescending()
                    .with(Hotspot.COLUMN_OCCURRENCE.orderByDescending())
                    .with(MemoryLocation.COLUMN_LATITUDE.orderByAscending())
                    .with(MemoryLocation.COLUMN_LONGITUDE.orderByAscending())
                    .with(MemoryLocation.COLUMN_ALTITUDE.orderByAscending());
        if (orderBy == null) {
            return null;
        }
        
        ExpressionToken limitToken = new ExpressionToken
                (limit <= 0 ? Constants.HOTSPOT_PRE_CANDIDATE_LIMIT : limit);
        
        query.setGroupBy(groupByToken);
        query.setOrderBy(orderBy);
        query.setLimit(limitToken);
        
        List<DatabaseObject> objects =
                reader.query(query, Hotspot.class);
        if (objects == null || objects.size() <= 0) {
            return null;
        }
        
        List<Hotspot> hotspots = new ArrayList<Hotspot>();
        
        for (DatabaseObject o: objects) {
            if (o instanceof Hotspot) {
                hotspots.add((Hotspot)o);
            }
        }
        
        return hotspots;
	}

}
