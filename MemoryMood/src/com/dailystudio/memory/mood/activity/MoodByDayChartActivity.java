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

public class MoodByDayChartActivity extends MemoryPeroidChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mood_by_day_chart);
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_mood_by_day_chart, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_add_mood: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), WhatIsYourMoodActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
			
			case R.id.menu_list_mood: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), MoodByDayListActivity.class);
				i.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}	
			
			case R.id.menu_mood_by_week: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), MoodByWeekChartActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}	
			
			case R.id.menu_share: {
        		final long now = System.currentTimeMillis();
        		
        		long timeOfDay = getPeroidStart();
        		if (timeOfDay <= 0) {
        			timeOfDay = now;
        		}
        		
        		final String subject = getString(R.string.share_subject_day_moods);
        		final String content = String.format("%s(%4d/%d/%d)",
        				getString(R.string.share_content_name),
        				CalendarUtils.getYear(timeOfDay),
        				CalendarUtils.getMonth(timeOfDay) + 1,
        				CalendarUtils.getDay(timeOfDay));
        		
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
				R.id.mood_by_day_fragment);
		
		if (fragment instanceof MemoryChartFragment<?> == false) {
			return null;
		}
		
		return (MemoryChartFragment<?>)fragment;
	}
	
}
