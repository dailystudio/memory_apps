package com.dailystudio.memory.where.loader;

import android.content.Context;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.memory.loader.ProjectedPeroidDatabaseObjectsLoader;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdspotHistorySummary;

public class IdentifiedHotspotListLoader 
	extends ProjectedPeroidDatabaseObjectsLoader<IdspotHistory, IdspotHistorySummary> {
	
	public IdentifiedHotspotListLoader(Context context) {
		super(context);
	}


	public IdentifiedHotspotListLoader(Context context, long start, long end) {
		super(context, start, end);
	}
	
	@Override
	protected Class<IdspotHistory> getObjectClass() {
		return IdspotHistory.class;
	}
	
	@Override
	protected Class<IdspotHistorySummary> getProjectionClass() {
		return IdspotHistorySummary.class;
	}
    
	@Override
    protected Query getQuery(Class<IdspotHistory> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		
		if (end <= start) {
			return super.getQuery(getObjectClass());
		}
		
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(getObjectClass());
		
		Query query = builer.getQueryForIntersect(
				IdspotHistory.COLUMN_TIME, 
				IdspotHistory.COLUMN_DURATION,
				start, end);	
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
				query.getSelection();
		
		IdspotHistory history = 
				IdspotHistoryDatabaseModal.getLastHistory(getContext());
		if (history == null || history.getDuration() > 0) {
			return query;
		}
		
		ExpressionToken moreSelToken = null;
		if (history.getTime() < end) {
			moreSelToken = IdspotHistory.COLUMN_ID.eq(history.getId());
		}
		
		if (moreSelToken != null) {
			if (selToken == null) {
				selToken = moreSelToken;
			} else {
				selToken = selToken.or(moreSelToken);
			}
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken groupByToken = 
				IdspotHistory.COLUMN_IDENTITY.groupBy();
		if (groupByToken != null) {
			query.setGroupBy(groupByToken);
		}
		
		OrderingToken orderByToken =
				IdspotHistorySummary.COLUMN_SUM_DURATION.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
    }
    
}
