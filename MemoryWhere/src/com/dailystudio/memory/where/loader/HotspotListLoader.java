package com.dailystudio.memory.where.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.loader.ProjectedPeroidDatabaseObjectsLoader;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;
import com.dailystudio.memory.where.hotspot.HotspotFinder;

public class HotspotListLoader extends ProjectedPeroidDatabaseObjectsLoader<MemoryLocation, Hotspot> {
	
	private long mMaxHotspot = Constants.HOTSPOT_PRE_CANDIDATE_LIMIT;
	
	public HotspotListLoader(Context context, long maxspot) {
		super(context);
	
		mMaxHotspot = maxspot;
	}

	public HotspotListLoader(Context context, 
			long start, long end, long maxspot) {
		super(context, start, end);

		mMaxHotspot = maxspot;
	}

	@Override
	protected Class<MemoryLocation> getObjectClass() {
		return MemoryLocation.class;
	}
	
	@Override
	protected Class<Hotspot> getProjectionClass() {
		return Hotspot.class;
	}
	
	@Override
	protected Query getQuery(Class<MemoryLocation> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken groupByToken = 
				MemoryLocation.COLUMN_LATITUDE.groupBy()
					.with(MemoryLocation.COLUMN_LONGITUDE.groupBy())
					.with(MemoryLocation.COLUMN_ALTITUDE.groupBy());
		if (groupByToken == null) {
			return query;
		}
		
		OrderingToken orderBy =
				Hotspot.COLUMN_SUM_DURATION.orderByDescending()
					.with(Hotspot.COLUMN_OCCURRENCE.orderByDescending())
					.with(MemoryLocation.COLUMN_LATITUDE.orderByAscending())
					.with(MemoryLocation.COLUMN_LONGITUDE.orderByAscending())
					.with(MemoryLocation.COLUMN_ALTITUDE.orderByAscending());
		if (orderBy == null) {
			return query;
		}
		
		ExpressionToken limitToken = new ExpressionToken
				(mMaxHotspot <= 0 ? Constants.HOTSPOT_PRE_CANDIDATE_LIMIT : mMaxHotspot);
		
		query.setGroupBy(groupByToken);
		query.setOrderBy(orderBy);
		query.setLimit(limitToken);
		
		return query;
	}
	
	@Override
	protected List<Hotspot> onLoadInBackground() {
		List<Hotspot> candidates = super.onLoadInBackground();
		if (candidates == null) {
			return null;
		}
		
		final HotspotFinder finder = new HotspotFinder();
		
		return finder.findHotspot(candidates);
	}

}
