package com.dailystudio.memory.application.loader;

import android.content.Context;

import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.loader.PeroidDatabaseObjectsLoader;

public class ActivityUsagesListLoader extends PeroidDatabaseObjectsLoader<Usage> {

	private String mPackageName;
	
	public ActivityUsagesListLoader(Context context, long start, long end, 
			String pkgName) {
		super(context, start, end);
		
		mPackageName = pkgName;
	}

	@Override
	protected Class<Usage> getObjectClass() {
		return Usage.class;
	}
	
	@Override
	protected Query getQuery(Class<Usage> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		Logger.debug("loader[%s], peroid = [%s - %s]", 
				this,
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		Query query = null;
		
		if (end <= start) {
			query = super.getQuery(getObjectClass());
		} else {
			TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(getObjectClass());
			
			query = builer.getQueryForIntersect(
					Usage.COLUMN_TIME, 
					Usage.COLUMN_DURATION,
					start, end);
		}
		
		
		OrderingToken orderByToken = Usage.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		if (mPackageName == null) {
			return query;
		}
		
		final Context context = getContext();
		
		Integer[] compIds = ArrayUtils.toIntegerArray(
				UsageComponentDatabaseModal.getComponentIds(context, mPackageName));
		if (compIds == null || compIds.length <= 0) {
			return query;
		}
		
		ExpressionToken selCompIdsToken = 
				Usage.COLUMN_COMPONENT_ID.inValues(compIds);
		Logger.debug("selCompIdsToken = %s", selCompIdsToken);
		
		ExpressionToken selToken = query.getSelection();

		if (selCompIdsToken != null) {
			if (selToken == null) {
				selToken = selCompIdsToken;
			} else {
				selToken = selToken.and(selCompIdsToken);
			}
			
			query.setSelection(selToken);
		}
	
		return query;
	}
	
}
