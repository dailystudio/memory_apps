package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseCursorLoader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;

public class PluginCategoryLoader extends DatabaseCursorLoader {

	public PluginCategoryLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryPluginActivityCategoryObject.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken orderByToken =
			MemoryPluginActivityCategoryObject.COLUMN_CATEGORY.orderByAscending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		OrderingToken groupByToken =
				MemoryPluginActivityCategoryObject.COLUMN_CATEGORY.groupBy();
		if (orderByToken != null) {
			query.setGroupBy(groupByToken);
		}
		
		return query;
	}
	
}
