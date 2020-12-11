package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;

public class NowWhereIdspotListLoader 
	extends DatabaseObjectsLoader<IdentifiedHotspot> {
	
	public NowWhereIdspotListLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<IdentifiedHotspot> getObjectClass() {
		return IdentifiedHotspot.class;
	}
    
	@Override
    protected Query getQuery(Class<IdentifiedHotspot> klass) {
		Query query = super.getQuery(klass);
		
		OrderingToken orderByToken =
				IdentifiedHotspot.COLUMN_IDENTITY.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
    }
    
}
