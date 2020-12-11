package com.dailystudio.memory.mood.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.mood.R;

public class WeekdaysListActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_weekdays_list);
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("object = %s", itemData);
		
		if (itemData instanceof TimeCapsule == false) {
			return;
		}

		TimeCapsule object = (TimeCapsule)itemData;
		
		final long time = object.getTime();

		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), MoodByDayChartActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		i.putExtra(Constants.EXTRA_PEROID_START, time);
		
		ActivityLauncher.launchActivity(this, i);
	}

	
}
