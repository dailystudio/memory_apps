package com.dailystudio.memory.application.appwidget;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;

public class HourlyFavoriteAppsWidgetDataAsyncTask extends
		FavoriteAppsWidgetDataAsyncTask{

	public HourlyFavoriteAppsWidgetDataAsyncTask(Context context) {
		super(context, false, Constants.FAVORITE_CLASS_WEEK);
	}

	@Override
	protected Query getQuery(Class<FavoriteApp> klass) {
		Query query = new Query(FavoriteApp.class);
		
		final int fclass = FavoriteApp.timeToFavoriteClass(
				System.currentTimeMillis());
		Logger.debug("now = %s, fclass = %s",
				CalendarUtils.timeToReadableString(System.currentTimeMillis()),
				FavoriteApp.favoriteClassToString(fclass));
		ExpressionToken selToken = 
				FavoriteApp.COLUMN_FAVORTIE_CLASS.eq(fclass);
		if (!isIncludeSysApps() && selToken != null) {
			selToken = selToken.and(FavoriteApp.COLUMN_SYSTEM_APP.eq(0));
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		final int count = getContext().getResources().getInteger(
				R.integer.config_app_widget_favorties_count);
		
		ExpressionToken limitToken = new ExpressionToken(count);
			
		query.setLimit(limitToken);
		
		return query;
	}

	@Override
	protected ComponentName getAppWidgetProvider() {
		return new ComponentName(getContext(), 
				HourlyFavoriteAppsWidgetProvider.class);
	}

	@Override
	protected Class<FavoriteApp> getObjectClass() {
		return FavoriteApp.class;
	}
	
}
