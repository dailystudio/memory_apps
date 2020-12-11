package com.dailystudio.memory.where.activity;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.MemoryPeroidChartActivity;
import com.dailystudio.memory.fragment.MemoryChartFragment;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.fragment.IdspotWeekStatChartFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class IdspotWeekStatChartActivity extends MemoryPeroidChartActivity {

	private final static int[] PEROID_FRAGMENT_IDS = {
		R.id.idspot_week_stat_fragment,
	};
	
	private int mIdspotId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_idspot_week_stat);
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);

		mIdspotId = intent.getIntExtra(Constants.EXTRA_IDSPOT_ID, -1);
		if (mIdspotId != -1) {
			MemoryChartFragment<?> fragment = getChartFragment();
			if (fragment instanceof IdspotWeekStatChartFragment) {
				((IdspotWeekStatChartFragment)fragment).attachToIdspot(mIdspotId);
			}
		}
		
		Logger.debug("start = %s", CalendarUtils.timeToReadableString(getPeroidStart()));
		Logger.debug("end = %s", CalendarUtils.timeToReadableString(getPeroidEnd()));
		Logger.debug("mIdspotId = %d", mIdspotId);
		
		String title = getString(R.string.activity_idspot_week_stat);
		
		final long weekStart = getPeroidStart();
		if (weekStart > 0) {
			final String weekstr = String.format(getString(
					R.string.idspot_stat_label_week_templ), 
					CalendarUtils.getWeek(weekStart));
			title = String.format("%s (%s)",
					title, 
					weekstr);
		}
		
		getCompatibleActionBar().setTitle(title);
	}
	
	@Override
	protected MemoryChartFragment<?> getChartFragment() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.idspot_week_stat_fragment);
		
		if (fragment instanceof IdspotWeekStatChartFragment == false) {
			return null;
		}
		
		return (IdspotWeekStatChartFragment)fragment;
	}

	@Override
	protected int[] listPeroidBaseFragmentIds() {
		return PEROID_FRAGMENT_IDS;
	}
	
}
