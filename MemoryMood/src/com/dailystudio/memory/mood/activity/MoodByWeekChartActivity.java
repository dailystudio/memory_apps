package com.dailystudio.memory.mood.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.activity.MemoryPeroidChartActivity;
import com.dailystudio.memory.fragment.MemoryChartFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MoodByWeekChartActivity extends MemoryPeroidChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mood_by_week_chart);
	}
	
	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_mood_by_week_chart, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_list_weekdays: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), WeekdaysListActivity.class);
				i.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());
				i.putExtra(Constants.EXTRA_PEROID_END, getPeroidEnd());
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}	
			
			case R.id.menu_list_weeks: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), WeeksListActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}	
			
			case R.id.menu_share: {
        		long weekStart = getPeroidStart();
        		long weekEnd = getPeroidEnd();
        		
        		if (weekStart <= 0 || weekEnd <= 0 || weekEnd <= weekStart) {
        			final long now = System.currentTimeMillis();
        			weekStart = CalendarUtils.getStartOfWeek(now);
        			weekEnd = CalendarUtils.getEndOfWeek(now);
        		}

        		final String subject = getString(R.string.share_subject_week_moods);
        		final String content = String.format("%s (%4d/%d/%d - %4d/%d/%d)",
        				getString(R.string.share_content_name),
        				CalendarUtils.getYear(weekStart),
        				CalendarUtils.getMonth(weekStart) + 1,
        				CalendarUtils.getDay(weekStart),
        				CalendarUtils.getYear(weekEnd),
        				CalendarUtils.getMonth(weekEnd) + 1,
        				CalendarUtils.getDay(weekEnd));
        		
				shareChart(getString(R.string.share_moods_title), 
						subject, content);
				
				return true;
			}	
		}
		
		return super.onOverflowItemSelected(item);
	}

	@Override
	protected MemoryChartFragment<?> getChartFragment() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.mood_by_week_fragment);
		
		if (fragment instanceof MemoryChartFragment<?> == false) {
			return null;
		}
		
		return (MemoryChartFragment<?>)fragment;
	}

}
