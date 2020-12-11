package com.dailystudio.memory.application.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.application.activity.DailyApplicationUsagesListActivity;
import com.dailystudio.memory.application.activity.MonthlyApplicationUsagesListActivity;
import com.dailystudio.memory.application.activity.WeeklyApplicationUsagesListActivity;
import com.dailystudio.nativelib.application.AndroidActivity;

public class UsageActivitiesListLoader extends AbsAsyncDataLoader<List<AndroidActivity>> {

	public UsageActivitiesListLoader(Context context) {
		super(context);
	}

	@Override
	public List<AndroidActivity> loadInBackground() {
		List<AndroidActivity> activities = new ArrayList<AndroidActivity>();
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		final Context appContext = context.getApplicationContext();
		if (appContext == null) {
			return null;
		}
		
		ComponentName[] comps = {
				new ComponentName(appContext, DailyApplicationUsagesListActivity.class),
				new ComponentName(appContext, WeeklyApplicationUsagesListActivity.class),
				new ComponentName(appContext, MonthlyApplicationUsagesListActivity.class),
		};
		
		for (ComponentName comp: comps) {
			activities.add(new AndroidActivity(comp));
		}
		
		return activities;
	}

}
