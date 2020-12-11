package com.dailystudio.memory.boot.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.CountObject;
import com.dailystudio.app.dataobject.loader.ProjectedDatabaseObjectsLoader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.boot.MemoryBoot;

public class MemoryBootCountLoader extends ProjectedDatabaseObjectsLoader<MemoryBoot, CountObject> {

	public MemoryBootCountLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<MemoryBoot> getObjectClass() {
		return MemoryBoot.class;
	}
	
	@Override
	protected Class<CountObject> getProjectionClass() {
		return CountObject.class;
	}

	@Override
	protected Query getQuery(Class<MemoryBoot> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
			MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		return query;
	}

}
