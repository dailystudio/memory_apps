package com.dailystudio.memory.application.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.application.databaseobject.UselessApp;

public class UselessAppsLoader extends DatabaseObjectsLoader<UselessApp> {

	public UselessAppsLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<UselessApp> getObjectClass() {
		return UselessApp.class;
	}
	
	@Override
	protected Query getQuery(Class<UselessApp> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken orderByToken = 
				UselessApp.COLUMN_NOT_USED_RECENTLY.orderByDescending()
					.with(UselessApp.COLUMN_NOT_UPDATED_RECENTLY.orderByDescending())
					.with(UselessApp.COLUMN_RECENT_USED_TIME.orderByAscending())
					.with(UselessApp.COLUMN_RECENT_UPDATED_TIME.orderByAscending())
					.with(UselessApp.COLUMN_PACKAGE_NAME.orderByAscending());
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return query;
	}
	
}
