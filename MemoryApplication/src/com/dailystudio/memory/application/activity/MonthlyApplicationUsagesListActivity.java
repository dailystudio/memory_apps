package com.dailystudio.memory.application.activity;

import java.text.SimpleDateFormat;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MonthlyApplicationUsagesListActivity
	extends ApplicationUsagesListActivity {

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_app_usage_monthly, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_list_months: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), MonthsListActivity.class);
				
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
			
			mPeroidStart = CalendarUtils.getStartOfMonth(now);
			mPeroidEnd = CalendarUtils.getEndOfMonth(now);
			
			intent.putExtra(Constants.EXTRA_PEROID_START, mPeroidStart);
			intent.putExtra(Constants.EXTRA_PEROID_END, mPeroidEnd);
			
			start = mPeroidStart;
			end = mPeroidEnd;
		}
		
		String title = getString(R.string.activity_monthly_app_usage_list);
		
		final long monthStart = start;
		Logger.debug("monthStart = %s", CalendarUtils.timeToReadableString(monthStart));
		if (monthStart > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.month_pattern));
			title = String.format("%s (%s)",
					title, 
					sdf.format(monthStart));
		}
		
		getCompatibleActionBar().setTitle(title);
	}
	
}
