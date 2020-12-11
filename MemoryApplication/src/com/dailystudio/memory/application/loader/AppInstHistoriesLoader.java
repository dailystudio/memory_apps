package com.dailystudio.memory.application.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;

public class AppInstHistoriesLoader extends DatabaseObjectsLoader<AppInstHistory> {

	public AppInstHistoriesLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<AppInstHistory> getObjectClass() {
		return AppInstHistory.class;
	}
	
	@Override
	protected Query getQuery(Class<AppInstHistory> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken orderByToken = AppInstHistory.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return query;
	}
	
}
