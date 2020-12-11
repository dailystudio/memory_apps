package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.loader.PeroidDatabaseObjectsLoader;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

public class LocationObjectListLoader extends PeroidDatabaseObjectsLoader<MemoryLocation> {

	public LocationObjectListLoader(Context context) {
		super(context);
	}

	public LocationObjectListLoader(Context context, long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<MemoryLocation> getObjectClass() {
		return MemoryLocation.class;
	}
	
	@Override
	protected Query getQuery(Class<MemoryLocation> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken orderByToken =
			TimeCapsule.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
	}
	
}
