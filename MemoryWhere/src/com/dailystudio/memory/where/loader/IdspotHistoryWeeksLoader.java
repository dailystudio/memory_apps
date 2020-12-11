package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.memory.loader.WeeksLoader;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;

public class IdspotHistoryWeeksLoader extends WeeksLoader<IdspotHistory> {

	public IdspotHistoryWeeksLoader(Context context) {
		super(context);
	}

	public IdspotHistoryWeeksLoader(Context context, long start, long end) {
		super(context, start, end);
	}
	
	@Override
	protected Class<IdspotHistory> getObjectClass() {
		return IdspotHistory.class;
	}

}
