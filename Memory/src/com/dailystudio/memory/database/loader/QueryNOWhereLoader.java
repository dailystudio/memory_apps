package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.querypieces.NOWhere;

public class QueryNOWhereLoader extends AbsAsyncDataLoader<String> {

	public QueryNOWhereLoader(Context context) {
		super(context);
	}

	
	@Override
	public String loadInBackground() {
		final Context context = getContext();
		
		String locName = null;
		
		NOWhere where = new NOWhere(context);
		if (where != null) {
			locName = where.getLocationName();
		}
		
		return locName;
	}
	
}
