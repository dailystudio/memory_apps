package com.dailystudio.memory.mood.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dailystudio.memory.appwidget.MemoryAppWidgetProvider;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.R;

public class MoodDayChartWidgetProvider extends MemoryAppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
    		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
    				R.layout.widget_mood_day_chart);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        
		updateWidgetWithDataObjects(context, MemoryMood.class, appWidgetIds);
	}
	
	@Override
	protected Intent getDataServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), MoodAppWidgetDataService.class);

		return i;
	}
	
}
