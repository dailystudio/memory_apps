package com.dailystudio.memory.where.activity;

import com.dailystudio.memory.activity.MemoryPeroidChartActivity;
import com.dailystudio.memory.fragment.MemoryChartFragment;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.fragment.IdspotPieStatChartFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class IdspotDayStatChartActivity extends MemoryPeroidChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_idspot_day_stat);
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		String title = getString(R.string.activity_idspot_day_stat);
		
		final long dayStart = getPeroidStart();
		if (dayStart > 0) {
			title = String.format("%s (%s)",
					title, 
					DateTimePrintUtils.printTimeStringWithoutTime(
							this, dayStart));
		}
		
		getCompatibleActionBar().setTitle(title);
	}
	
	@Override
	protected MemoryChartFragment<?> getChartFragment() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.idspot_day_stat_fragment);
		
		if (fragment instanceof IdspotPieStatChartFragment == false) {
			return null;
		}
		
		return (IdspotPieStatChartFragment)fragment;
	}

}
