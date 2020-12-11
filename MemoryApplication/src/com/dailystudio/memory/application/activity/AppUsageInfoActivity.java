package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.fragment.ActivityUsagesListFragment;
import com.dailystudio.memory.ui.SlidingDrawer;

public class AppUsageInfoActivity extends MemoryPeroidBasedActivity {

	private static final int[] PEROID_FRAGMENT_IDS = {
		R.id.fragment_app_usage_chart,
//		R.id.fragment_activity_usage_list,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_app_usage_info);
		
		setupViews();
	}
	
	private void setupViews() {
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		
		if ((end - start) < CalendarUtils.DAY_IN_MILLIS) {
			SlidingDrawer slidingDrawer = 
					(SlidingDrawer) findViewById(R.id.sliding_drawer);
			if (slidingDrawer != null) {
				slidingDrawer.animateOpen();
				slidingDrawer.lock();
			}
		}
	}

	@SuppressWarnings("unused")
	private ActivityUsagesListFragment findUsagesListFragment() {
		Fragment fragment = 
			getSupportFragmentManager().findFragmentById(
				R.id.act_usage_list_fragment);
		if (fragment instanceof ActivityUsagesListFragment == false) {
			return null;
		}
		
		return (ActivityUsagesListFragment)fragment;
	}

	@Override
	protected int[] listPeroidBaseFragmentIds() {
		return PEROID_FRAGMENT_IDS;
	}

}
