package com.dailystudio.memory.boot.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.record.MemoryRecord;

public class BootRecordsLoader extends DatabaseObjectsLoader<MemoryRecord> {

	public BootRecordsLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<MemoryRecord> getObjectClass() {
		return MemoryRecord.class;
	}
	
	@Override
	protected Query getQuery(Class<MemoryRecord> klass) {
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
