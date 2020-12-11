package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.dataobject.WeekObject;

public class WeeksListActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_weeks_list);
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("object = %s", itemData);
		
		if (itemData instanceof WeekObject == false) {
			return;
		}
		
		WeekObject week = (WeekObject)itemData;
		
		final long time = week.getTime();
		
		final long weekStart = CalendarUtils.getStartOfWeek(time);
		final long weekEnd = CalendarUtils.getEndOfWeek(time);
		Logger.debug("%s - %s", 
				CalendarUtils.timeToReadableStringWithoutTime(weekStart),
				CalendarUtils.timeToReadableStringWithoutTime(weekEnd));

		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), WeeklyApplicationUsagesListActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		i.putExtra(Constants.EXTRA_PEROID_START, weekStart);
		i.putExtra(Constants.EXTRA_PEROID_END, weekEnd);
		
		ActivityLauncher.launchActivity(this, i);
	}
	
}
