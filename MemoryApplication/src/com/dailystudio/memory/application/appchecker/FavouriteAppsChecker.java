package com.dailystudio.memory.application.appchecker;

import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.ActivityUsageStatistics;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.databaseobject.Usage;

import android.content.Context;

public class FavouriteAppsChecker extends AbsFavouriteAppsChecker {
	
	private long mPeroidStart;
	private long mPeroidEnd;
	
	public FavouriteAppsChecker(Context context, long start, long end, int favouriteClass) {
		super(context, favouriteClass);
		
		mPeroidStart = start;
		mPeroidEnd = end;
	}

	@Override
	protected Query getCheckQuery() {
		Logger.debug("ANALYZE FAVORITE APPS: [%s - %s], favoriteClas = %s",
				CalendarUtils.timeToReadableString(mPeroidStart),
				CalendarUtils.timeToReadableString(mPeroidEnd),
				FavoriteApp.favoriteClassToString(getFavouriteClass()));
		final long start = mPeroidStart;
		final long end = mPeroidEnd;
		Logger.debug("peroid = [%s - %s]", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		Query query = null;
		
		if (end <= start) {
			return null;
		}
		
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(Usage.class);
			
		query = builer.getQueryForIntersect(
				Usage.COLUMN_TIME, 
				Usage.COLUMN_DURATION,
				start, end);

/*		Integer[] filterCompIds = 
				UsageComponentDatabaseModal.getSystemComponentIds(mContext);
		if (filterCompIds != null && filterCompIds.length > 0) {
			ExpressionToken selCompIdsToken = 
					Usage.COLUMN_COMPONENT_ID.outOfValues(filterCompIds);
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
		}
*/		
		OrderingToken groupBy = Usage.COLUMN_COMPONENT_ID.groupBy();
		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
		
		OrderingToken orderByToken = ActivityUsageStatistics.COLUMN_DURATION_SUM.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return query;
	}

}
