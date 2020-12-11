package com.dailystudio.memory.where.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CurrentIdspotListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final long now = System.currentTimeMillis();

		final long weekStart = CalendarUtils.getStartOfWeek(now);
		final long weekEnd = CalendarUtils.getEndOfWeek(now);
		Logger.debug("%s - %s", 
				CalendarUtils.timeToReadableStringWithoutTime(weekStart),
				CalendarUtils.timeToReadableStringWithoutTime(weekEnd));

		Intent launchIntent = getIntent();
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), IdentifiedHotspotListActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		i.putExtra(Constants.EXTRA_PEROID_START, weekStart);
		i.putExtra(Constants.EXTRA_PEROID_END, weekEnd);

		if (launchIntent != null) {
			i.putExtra(Constants.EXTRA_QUESTION_ID,
					launchIntent.getIntExtra(Constants.EXTRA_QUESTION_ID,
							-1));
		}
		
		ActivityLauncher.launchActivity(this, i);
		
		finish();
	}
	
}
