package com.dailystudio.memory.application;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.application.appchecker.FavouriteAppsChecker;
import com.dailystudio.memory.application.appchecker.HourlyFavouriteAppsChecker;
import com.dailystudio.memory.task.Task;

public class FavoriteAppsCheckTask extends Task {

	public FavoriteAppsCheckTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onDestroy(Context context, long time) {
		super.onDestroy(context, time);
	}

	@Override
	public void onExecute(Context context, long time) {
		FavouriteAppsChecker checker = null;
		
		final long now = System.currentTimeMillis();

		checker = new FavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 7, now, 
				Constants.FAVORITE_CLASS_WEEK);
		checker.runIfOnTime();

		checker = new FavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 31, now, 
				Constants.FAVORITE_CLASS_MONTH);
		checker.runIfOnTime();

		checker = new HourlyFavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 31, now, 
				Constants.FAVORITE_CLASS_DAY_0_8);
		checker.runIfOnTime();
		
		checker = new HourlyFavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 31, now, 
				Constants.FAVORITE_CLASS_DAY_9_12);
		checker.runIfOnTime();
		
		checker = new HourlyFavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 31, now, 
				Constants.FAVORITE_CLASS_DAY_13_18);
		checker.runIfOnTime();
		
		checker = new HourlyFavouriteAppsChecker(context,
				now - CalendarUtils.DAY_IN_MILLIS * 31, now, 
				Constants.FAVORITE_CLASS_DAY_19_23);
		checker.runIfOnTime();
	}

}
