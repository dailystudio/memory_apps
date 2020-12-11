package com.dailystudio.memory.boot.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.memory.boot.Constants;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.activity.ActionBar;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseScreenOnDistribChartActivity extends MemoryPeroidBasedActivity {

	private static int[] peroidFragmentIds = {
		R.id.screen_on_distrib_chart_fragment,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_screen_on_distrub_chart);

		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_base_screen_on_distrib_chart, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_screen_on_list) {
				Intent listIntent = new Intent();
				
				listIntent.setClass(getApplicationContext(), 
						ScreenOnListActivity.class);
				
				listIntent.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());
				listIntent.putExtra(Constants.EXTRA_PEROID_END, getPeroidEnd());
				
				Intent intent = getIntent();
				if (intent != null) {
					final int[] filterWeekdays = intent.getIntArrayExtra(
							Constants.EXTRA_FILTER_WEEKDAYS);
					
					if (filterWeekdays != null) {
						listIntent.putExtra(Constants.EXTRA_FILTER_WEEKDAYS, 
								filterWeekdays);
					}
				}

				ActivityLauncher.launchActivity(this, listIntent);
				
				return true;
		} 
		
		return super.onOverflowItemSelected(item);
	}

	@Override
	protected int[] listPeroidBaseFragmentIds() {
		return peroidFragmentIds;
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		int title = intent.getIntExtra(Constants.EXTRA_ACTIVITY_TITLE_RESID, -1);
		if (title == -1) {
			return;
		}
		
		final ActionBar actbar = getCompatibleActionBar();
		if (actbar == null) {
			return;
		}

		actbar.setTitle(getString(title));
	}
	
}
