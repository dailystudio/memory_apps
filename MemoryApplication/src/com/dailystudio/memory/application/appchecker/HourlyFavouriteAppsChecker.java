package com.dailystudio.memory.application.appchecker;

import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.databaseobject.Usage;

public class HourlyFavouriteAppsChecker extends FavouriteAppsChecker {
	
	public HourlyFavouriteAppsChecker(Context context, long start, long end, int favouriteClass) {
		super(context, start, end, favouriteClass);
	}

	@Override
	protected Query getCheckQuery() {
		Query query = super.getCheckQuery();
		
		ExpressionToken baseExp = null;
		if (query != null) {
			baseExp = query.getSelection();
		}
		
		final int favoriteClass = getFavouriteClass();
		
		final long range[] = getTimeRange(getFavouriteClass());
		Logger.debug("ANALYZE FAVORITE APPS: [%s - %s], favoriteClas = %s",
				CalendarUtils.durationToReadableString(range[0]),
				CalendarUtils.durationToReadableString(range[1]),
				FavoriteApp.favoriteClassToString(favoriteClass));
		
		Query rangeQuery = null;
		
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(Usage.class);
		
		ExpressionToken timeExp = new ExpressionToken(
				String.format("( %s + %d )",
						Usage.COLUMN_TIME.timeInDay(),
						CalendarUtils.getTimezoneOffeset()));
		
		rangeQuery = builer.getQueryForIntersect(
				timeExp, 
				new ExpressionToken(Usage.COLUMN_DURATION),
				range[0], range[1]);
		
		if (rangeQuery != null) {
			ExpressionToken rangeExp =
					rangeQuery.getSelection();
			if (rangeExp != null) {
				baseExp = baseExp.and(rangeExp);
			}
		}
		
		Logger.debug("FULL QUERY: %s", baseExp);
		
		query.setSelection(baseExp);

		return query;
	}
	
	private long[] getTimeRange(int favoriteClass) {
		long[] range = new long[2];
		switch (favoriteClass) {
			case Constants.FAVORITE_CLASS_DAY_0_8:
				range[0] = 0;
				range[1] = CalendarUtils.HOUR_IN_MILLIS * 9 - 1;
				break;
				
			case Constants.FAVORITE_CLASS_DAY_9_12:
				range[0] = CalendarUtils.HOUR_IN_MILLIS * 9 ;
				range[1] = CalendarUtils.HOUR_IN_MILLIS * 13 - 1;
				break;
				
			case Constants.FAVORITE_CLASS_DAY_13_18:
				range[0] = CalendarUtils.HOUR_IN_MILLIS * 13 ;
				range[1] = CalendarUtils.HOUR_IN_MILLIS * 19 - 1;
				break;

			case Constants.FAVORITE_CLASS_DAY_19_23:
				range[0] = CalendarUtils.HOUR_IN_MILLIS * 19 ;
				range[1] = CalendarUtils.HOUR_IN_MILLIS * 24 - 1;
				break;

			default:
				range[0] = 0 ;
				range[1] = CalendarUtils.HOUR_IN_MILLIS * 24 - 1;
				break;

		}
		
		return range;
	}
	
	@Override
	protected String getCheckerPrefName() {
		String base = super.getCheckerPrefName();
		if (TextUtils.isEmpty(base)) {
			return base;
		}
		
		StringBuilder builder = new StringBuilder(base);
		builder.append('_');
		builder.append(getFavouriteClass());
		
		return builder.toString();
	}

}
