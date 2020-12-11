package com.dailystudio.memory.activity;

import com.dailystudio.memory.R;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.fragment.AbsMemoryListFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

public abstract class AbsMemoryListActivity extends ActionBarActivity {

	protected void onNewIntent(Intent intent) {
		AbsMemoryListFragment fragment = getListFragment();
		if (fragment == null) {
			return;
		}
		
		fragment.onNewIntent(intent);
	};

	protected int getListFragmentId() {
		return R.id.fragment_list;
	}
	
	protected AbsMemoryListFragment getListFragment() {
		final int fragmentId = getListFragmentId();
		
		Fragment fragment =
			getSupportFragmentManager().findFragmentById(fragmentId);
		
		if (fragment instanceof AbsMemoryListFragment == false) {
			return null;
		}
		
		return (AbsMemoryListFragment)fragment;
	}
	
}