package com.dailystudio.memory.boot.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseCursorLoader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.boot.MemoryBoot;

public class MemoryBootLoader extends DatabaseCursorLoader {

	public MemoryBootLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryBoot.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
			MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken orderByToken =
			TimeCapsule.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
	}
	
}
