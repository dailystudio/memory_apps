package com.dailystudio.memory.application.activity;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

import android.content.Intent;

public class DailyApplicationUsagesListActivity
	extends ApplicationUsagesListActivity {

	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		long start = getPeroidStart();
		long end = getPeroidEnd();
		if (start < 0 || end < 0) {
			final long now = System.currentTimeMillis();
			
			mPeroidStart = CalendarUtils.getStartOfDay(now);
			mPeroidEnd = CalendarUtils.getEndOfDay(now);
			
			intent.putExtra(Constants.EXTRA_PEROID_START, mPeroidStart);
			intent.putExtra(Constants.EXTRA_PEROID_END, mPeroidEnd);
			
			start = mPeroidStart;
			end = mPeroidEnd;
		}
		
		String title = getString(R.string.activity_daily_app_usage_list);
		
		final long dayStart = start;
		Logger.debug("dayStart = %s", CalendarUtils.timeToReadableString(dayStart));
		if (dayStart > 0) {
			title = String.format("%s (%s)",
					title, 
					DateTimePrintUtils.printTimeStringWithoutTime(
							this, dayStart));
		}
		
		getCompatibleActionBar().setTitle(title);
	}
	
}
