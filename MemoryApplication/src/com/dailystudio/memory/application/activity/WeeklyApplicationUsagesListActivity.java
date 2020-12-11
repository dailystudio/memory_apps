package com.dailystudio.memory.application.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WeeklyApplicationUsagesListActivity
	extends ApplicationUsagesListActivity {

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_app_usage_weekly, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_list_weeks: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), WeeksListActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}	
		}
		
		return super.onOverflowItemSelected(item);
	}

	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		long start = getPeroidStart();
		long end = getPeroidEnd();
		if (start < 0 || end < 0) {
			final long now = System.currentTimeMillis();
			
			mPeroidStart = CalendarUtils.getStartOfWeek(now);
			mPeroidEnd = CalendarUtils.getEndOfWeek(now);
			
			intent.putExtra(Constants.EXTRA_PEROID_START, mPeroidStart);
			intent.putExtra(Constants.EXTRA_PEROID_END, mPeroidEnd);
			
			start = mPeroidStart;
			end = mPeroidEnd;
		}
		
		String title = getString(R.string.activity_weekly_app_usage_list);
		
		final long weekStart = start;
		Logger.debug("weekStart = %s", CalendarUtils.timeToReadableString(weekStart));
		if (weekStart > 0) {
			final String weekstr = String.format(getString(
					R.string.app_usage_label_week_templ), 
					CalendarUtils.getWeek(weekStart));
			title = String.format("%s (%s)",
					title, 
					weekstr);
		}
		
		getCompatibleActionBar().setTitle(title);
	}
	
}
