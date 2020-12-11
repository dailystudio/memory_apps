package com.dailystudio.memory.boot.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.boot.Constants;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.datetime.DaysFilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ScreenOnDistribChartActivity extends BaseScreenOnDistribChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		
		Intent newIntent = new Intent(i);
		
		final long now = System.currentTimeMillis();
		
		newIntent.putExtra(Constants.EXTRA_PEROID_START, CalendarUtils.getStartOfMonth(now));
		newIntent.putExtra(Constants.EXTRA_PEROID_END, CalendarUtils.getEndOfMonth(now));

		setIntent(newIntent);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_screen_on_distrib_chart, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_screen_on_today) {
			Intent todayIntent = new Intent();
			
			todayIntent.setClass(getApplicationContext(), 
					BaseScreenOnDistribChartActivity.class);
			
			final long now = System.currentTimeMillis();
			
			todayIntent.putExtra(Constants.EXTRA_PEROID_START, CalendarUtils.getStartOfDay(now));
			todayIntent.putExtra(Constants.EXTRA_PEROID_END, CalendarUtils.getEndOfDay(now));
			todayIntent.putExtra(Constants.EXTRA_ACTIVITY_TITLE_RESID, 
					R.string.activity_screen_on_distrib_today);
			
			ActivityLauncher.launchActivity(this, todayIntent);
			
			return true;
		} else if (item.getItemId() == R.id.menu_screen_on_weekdays) {
			Intent weekdaysIntent = new Intent();
			
			weekdaysIntent.setClass(getApplicationContext(), 
					BaseScreenOnDistribChartActivity.class);
			
			final long now = System.currentTimeMillis();
			
			weekdaysIntent.putExtra(Constants.EXTRA_PEROID_START, CalendarUtils.getStartOfMonth(now));
			weekdaysIntent.putExtra(Constants.EXTRA_PEROID_END, CalendarUtils.getEndOfMonth(now));

			weekdaysIntent.putExtra(Constants.EXTRA_FILTER_WEEKDAYS, DaysFilter.FILTER_WEEKDAYS);
			weekdaysIntent.putExtra(Constants.EXTRA_ACTIVITY_TITLE_RESID, 
					R.string.activity_screen_on_distrib_weekdays);

			ActivityLauncher.launchActivity(this, weekdaysIntent);
			
			return true;
		} else if (item.getItemId() == R.id.menu_screen_on_weekends) {
			Intent weekendsIntent = new Intent();
			
			weekendsIntent.setClass(getApplicationContext(), 
					BaseScreenOnDistribChartActivity.class);
			
			final long now = System.currentTimeMillis();
			
			weekendsIntent.putExtra(Constants.EXTRA_PEROID_START, CalendarUtils.getStartOfMonth(now));
			weekendsIntent.putExtra(Constants.EXTRA_PEROID_END, CalendarUtils.getEndOfMonth(now));

			weekendsIntent.putExtra(Constants.EXTRA_FILTER_WEEKDAYS, DaysFilter.FILTER_WEEKENDS);
			weekendsIntent.putExtra(Constants.EXTRA_ACTIVITY_TITLE_RESID, 
					R.string.activity_screen_on_distrib_weekends);

			ActivityLauncher.launchActivity(this, weekendsIntent);
			
			return true;
		} 
		
		return super.onOverflowItemSelected(item);
	}

}
