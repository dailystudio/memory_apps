package com.dailystudio.memory.mood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.mood.appwidget.MoodAppWidgetDataService;
import com.dailystudio.memory.task.Task;

public class MoodWidgetDataUpdateCheckerTask extends Task {
	
	private final static String PREF_DATE_CHECK = "date-check";
	private final static String KEY_LAST_DATE = "last_date";
	
	public MoodWidgetDataUpdateCheckerTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		final boolean dateChanged = checkDateChanged(context);
		if (dateChanged == false) {
			return;
		}
		
		forceWidgetUpdate(context);
	}
	
	private void forceWidgetUpdate(Context context) {
		if (context == null) {
			return;
		}
		
		final Intent srvIntent = new Intent();
		
		srvIntent.setClass(context.getApplicationContext(), 
				MoodAppWidgetDataService.class);
		
		srvIntent.putExtra(Constants.EXTRA_APP_WIDGET_DATA_OBJECT_CLASS,
				MemoryMood.class.getName());
		
		context.startService(srvIntent);
	}

	final boolean checkDateChanged(Context context) {
		if (context == null) {
			return false;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(
				PREF_DATE_CHECK, Context.MODE_PRIVATE);
		if (pref == null) {
			return false;
		}
		
		final long now = System.currentTimeMillis();
		
		final long currDate = CalendarUtils.getStartOfDay(now);
		final long lastDate = pref.getLong(KEY_LAST_DATE, -1l);
		
		final boolean changed = 
				(lastDate == -1 || currDate != lastDate);
		Logger.debug("currDate = %d, lastDate = %d, changed = %s",
				currDate, lastDate, changed);
		
		if (changed) {
			Editor editor = pref.edit();
			if (editor != null) {
				editor.putLong(KEY_LAST_DATE, currDate);
				editor.commit();
			}
		}
		
		return changed;
	}
	
}
