package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.database.MemoryKeyNoteCache;
import com.dailystudio.memory.loader.PeroidDatabaseCursorLoader;

public class KeyNoteCachesLoader extends PeroidDatabaseCursorLoader {
	
	public KeyNoteCachesLoader(Context context, long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryKeyNoteCache.class;
	}

	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = new Query(klass);
		
		final long dayStart = getPeroidStart();
		final long dayEnd = getPeroidEnd();
		
		if (dayStart < dayEnd) {
			ExpressionToken selection = MemoryKeyNoteCache.COLUMN_DAY_START.eq(dayStart)
				.and(MemoryKeyNoteCache.COLUMN_DAY_END.eq(dayEnd));
			if (selection != null) {
				query.setSelection(selection);
			}
		}
		
		OrderingToken orderBy = TimeCapsule.COLUMN_TIME.orderByDescending();
		if (orderBy != null) {
			query.setOrderBy(orderBy);
		}
		
		return query;
	}

}
