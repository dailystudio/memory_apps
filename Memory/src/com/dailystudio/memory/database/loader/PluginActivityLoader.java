package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseCursorLoader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.database.MemoryPluginActivityObject;

public class PluginActivityLoader extends DatabaseCursorLoader {

	private String mCategory;

	public PluginActivityLoader(Context context, String category) {
		super(context);
		
		mCategory = category;
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryPluginActivityObject.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
			MemoryPluginActivityObject.COLUMN_CATEGORY.eq(mCategory);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken orderByToken =
			MemoryPluginActivityObject.COLUMN_USAGE_COUNT.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
	}

}
