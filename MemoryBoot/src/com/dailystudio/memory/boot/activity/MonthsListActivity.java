package com.dailystudio.memory.boot.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.dataobject.MonthObject;

public class MonthsListActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_months_list);
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("object = %s", itemData);
		
		if (itemData instanceof MonthObject == false) {
			return;
		}
		
		MonthObject month = (MonthObject)itemData;
		
		final long time = month.getTime();
		
		final long monthStart = CalendarUtils.getStartOfMonth(time);
		final long monthEnd = CalendarUtils.getEndOfMonth(time);
		Logger.debug("%s - %s", 
				CalendarUtils.timeToReadableStringWithoutTime(monthStart),
				CalendarUtils.timeToReadableStringWithoutTime(monthEnd));

		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), UptimeTrendsMonthChartActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		i.putExtra(Constants.EXTRA_PEROID_START, monthStart);
		i.putExtra(Constants.EXTRA_PEROID_END, monthEnd);
		
		ActivityLauncher.launchActivity(this, i);
	}
	
}
