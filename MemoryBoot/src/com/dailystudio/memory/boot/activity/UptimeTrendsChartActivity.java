package com.dailystudio.memory.boot.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class UptimeTrendsChartActivity extends MemoryPeroidBasedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_uptime_trends_chart);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}
	

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_uptime_trend_month_chart, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_list_months) {
			Intent i = new Intent();
			
			i.setClass(getApplicationContext(), MonthsListActivity.class);
			
			ActivityLauncher.launchActivity(this, i);
			
			return true;
		}
		
		return super.onOverflowItemSelected(item);
	}
	
}
