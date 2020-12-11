package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.loader.PeroidDatabaseCursorLoader;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

public class LocationCursorListLoader extends PeroidDatabaseCursorLoader {

	public LocationCursorListLoader(Context context) {
		super(context);
	}

	public LocationCursorListLoader(Context context, long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryLocation.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
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
