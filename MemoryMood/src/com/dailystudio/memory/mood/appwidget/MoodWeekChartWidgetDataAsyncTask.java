package com.dailystudio.memory.mood.appwidget;

import java.util.List;


import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.dailystudio.app.utils.BitmapUtils;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.appwidget.AppWidgetChartDataAsyncTask;
import com.dailystudio.memory.chart.ChartBuilder;
import com.dailystudio.memory.mood.Constants;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.mood.activity.MoodByDayChartActivity;
import com.dailystudio.memory.mood.activity.MoodByDayListActivity;
import com.dailystudio.memory.mood.activity.MoodByWeekChartActivity;
import com.dailystudio.memory.mood.activity.WhatIsYourMoodActivity;
import com.dailystudio.memory.mood.chart.WidgetMoodDayChartBuilder;

public class MoodWeekChartWidgetDataAsyncTask 
	extends AppWidgetChartDataAsyncTask<MemoryMood> {

	private WidgetMoodDayChartBuilder mChartBuilder;
	
	public MoodWeekChartWidgetDataAsyncTask(Context context, long timeOfDay) {
		super(context);

		if (timeOfDay <= 0) {
			timeOfDay = System.currentTimeMillis();;
		}

		mChartBuilder = new WidgetMoodDayChartBuilder(context, timeOfDay);
		
		setPeroid(CalendarUtils.getStartOfDay(timeOfDay), 
				CalendarUtils.getEndOfDay(timeOfDay));
	}

	@Override
	protected RemoteViews bindRemoteViews(List<MemoryMood> data) {
		final Context context = getContext();
		if (context == null) {
			return null;
		}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_mood_day_chart);
		
		fillRemoteViews(remoteViews);
		
		return remoteViews;
	}

	@Override
	protected ComponentName getAppWidgetProvider() {
		return new ComponentName(getContext(), 
				MoodDayChartWidgetProvider.class);
	}

	@Override
	protected Class<MemoryMood> getObjectClass() {
		return MemoryMood.class;
	}
	
	private void fillRemoteViews(RemoteViews remoteViews) {
		if (remoteViews == null) {
			return;
		}
		
		final Context context = getContext();
		if (context == null) {
			return;
		}
		
		final Context appContext = context.getApplicationContext();
		if (appContext == null) {
			return;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return;
		}
		
		final Object dataset = getDataSet();
		final Object renderer = getChartRenderer();
		
		final int spanX = res.getInteger(R.integer.aw_mood_day_chart_span_x);
		final int spanY = res.getInteger(R.integer.aw_mood_day_chart_span_y);
		
		int[] dimens = estimateDimensionInWidget(R.layout.widget_mood_day_chart, 
				R.id.widget_mchart_img, spanX, spanY);
		
		int chartWidth = dimens[0];
		int chartHeight = dimens[1];
		Logger.debug("Dimension Esitamted: [%d, %d]", 
				chartWidth, chartHeight);
		
		View chartView = mChartBuilder.getChart(dataset, renderer);
		
		final Bitmap bitmap = BitmapUtils.createViewSnapshot(appContext, chartView, 
				chartWidth, chartHeight);
		if (bitmap != null) {
			remoteViews.setImageViewBitmap(R.id.widget_mchart_img, bitmap);
		}
		
		bindClickOnChart(appContext, remoteViews);
		bindClickOnAddButton(appContext, remoteViews);
		bindClickOnListButton(appContext, remoteViews);
		bindClickOnWeekButton(appContext, remoteViews);
	}
	
	private void bindClickOnAddButton(Context context, RemoteViews remoteViews) {
		if (context == null || remoteViews == null) {
			return;
		}
		
        Intent i = new Intent();
        
		i.setClass(context.getApplicationContext(), WhatIsYourMoodActivity.class);
		i.putExtra(Constants.EXTRA_LAUNCHED_BY_APP_WIDGET, true);
		
        PendingIntent pendingIntent = 
        		PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_mchart_btn_add, pendingIntent);
	}

	private void bindClickOnListButton(Context context, RemoteViews remoteViews) {
		if (context == null || remoteViews == null) {
			return;
		}
		
        Intent i = new Intent();
		i.setClass(context, MoodByDayListActivity.class);
		i.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());

        PendingIntent pendingIntent = 
        		PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_mchart_btn_list, pendingIntent);
	}

	private void bindClickOnWeekButton(Context context, RemoteViews remoteViews) {
		if (context == null || remoteViews == null) {
			return;
		}
		
        Intent i = new Intent();
		i.setClass(context.getApplicationContext(), MoodByWeekChartActivity.class);

        PendingIntent pendingIntent = 
        		PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_mchart_btn_week, pendingIntent);
	}

	private void bindClickOnChart(Context context, RemoteViews remoteViews) {
		if (context == null || remoteViews == null) {
			return;
		}
		
        Intent i = new Intent();
		i.setClass(context.getApplicationContext(), MoodByDayChartActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = 
        		PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_mchart_img, pendingIntent);
	}

	@Override
	protected ChartBuilder<MemoryMood> createChartBuilder() {
		return mChartBuilder;
	}

}
