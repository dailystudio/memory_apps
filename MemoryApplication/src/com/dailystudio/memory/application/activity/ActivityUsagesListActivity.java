package com.dailystudio.memory.application.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.fragment.ActivityUsagesListFragment;

public class ActivityUsagesListActivity extends MemoryPeroidBasedActivity {

	private static final int[] PEROID_FRAGMENT_IDS = {
		R.id.act_usage_list_fragment,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_act_usage_list);
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
