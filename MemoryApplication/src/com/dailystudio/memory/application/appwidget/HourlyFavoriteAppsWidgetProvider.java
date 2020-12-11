package com.dailystudio.memory.application.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.appwidget.MemoryAppWidgetProvider;

public class HourlyFavoriteAppsWidgetProvider extends MemoryAppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
    		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
    				R.layout.widget_favortie_apps);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
     
        updateWidgetWithDataObjects(context, FavoriteApp.class, appWidgetIds);
	}
	
	@Override
	protected Intent getDataServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), FavoriteAppsWidgetDataService.class);

		return i;
	}
	
}
