package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.loader.PeroidDatabaseCursorLoader;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;

public class IdspotHistoryListLoader extends PeroidDatabaseCursorLoader {

	private int mIdspotId = -1;
	
	public IdspotHistoryListLoader(Context context, 
			long start, long end) {
		this(context, start, end, -1);
	}

	public IdspotHistoryListLoader(Context context, 
			long start, long end, int idspotId) {
		super(context, start, end);
		
		mIdspotId = idspotId;
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return IdspotHistory.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		OrderingToken orderByToken =
			TimeCapsule.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		if (mIdspotId <= 0) {
			return query;
		}
		
		ExpressionToken idspotSelToken = 
				IdspotHistory.COLUMN_IDSPOT_ID.eq(mIdspotId);
		if (idspotSelToken == null) {
			return query;
		}
		
		ExpressionToken selToken = query.getSelection();
		if (selToken == null) {
			selToken = idspotSelToken;
		} else {
			selToken.and(idspotSelToken);
		}
		
		query.setSelection(selToken);

		return query;
	}
	
}
