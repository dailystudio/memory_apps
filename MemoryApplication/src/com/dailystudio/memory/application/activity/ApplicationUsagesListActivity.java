package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.ApplicationUsageStatistics;
import com.dailystudio.memory.application.fragment.ApplicationUsagesListFragment;
import com.dailystudio.memory.application.ui.AppUsageInfoWindow;

public class ApplicationUsagesListActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {
	
	private static final int[] PEROID_FRAGMENT_IDS = {
		R.id.app_usage_list_fragment,
	};
	
	private AppUsageInfoWindow mUsageInfoWindow;
	
	private volatile boolean mFilterSystemApps = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_app_usage_list);
		
		setupViews();
	}
	
	private void setupViews() {
		mUsageInfoWindow = (AppUsageInfoWindow)findViewById(
				R.id.app_usage_info_window);
	}
	
	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_app_usage, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_no_sys_apps: {
				if (mFilterSystemApps) {
					item.setIcon(R.drawable.ic_menu_filter_sys_apps);
					item.setTitle(R.string.menu_filter_sys_apps);
				} else {
					item.setIcon(R.drawable.ic_menu_filter_non_apps);
					item.setTitle(R.string.menu_filter_non_apps);
				}

				mFilterSystemApps = !mFilterSystemApps;
				
				final ApplicationUsagesListFragment fragment = 
						findUsagesFragment();
				if (fragment != null) {
					fragment.setFilterFlags(mFilterSystemApps ? 
							Constants.USER_APP_ONLY_FILTER_FLAGS 
							: Constants.ALL_APP_FILTER_FLAGS);
				}
				 
				return true;
			}
			
/*			case R.id.menu_usage_history: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), 
						ActivityUsagesListActivity.class);
				
				i.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());
				i.putExtra(Constants.EXTRA_PEROID_END, getPeroidEnd());
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
*/			
		}
		
		return super.onOverflowItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (hideAppUsageInfo()) {
			return;
		} 
		
		super.onBackPressed();
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("itemData = %s", itemData);
		if (mUsageInfoWindow == null) {
			return;
		}
		
		if (itemData instanceof ApplicationUsageStatistics == false) {
			return;
		}
		
		ApplicationUsageStatistics appUs = 
			(ApplicationUsageStatistics) itemData;
		if (appUs.isResourcesResolved() == false) {
			appUs.resolveResources(this);
		}
	
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), AppUsageInfoActivity.class);
		
		i.putExtra(Constants.EXTRA_PEROID_START, getPeroidStart());
		i.putExtra(Constants.EXTRA_PEROID_END, getPeroidEnd());
		i.putExtra(Constants.EXTRA_APP_PACKAGE, appUs.getPackageName());
		
		ActivityLauncher.launchActivity(this, i);
	}
	
	private boolean hideAppUsageInfo() {
		if (mUsageInfoWindow != null && mUsageInfoWindow.isShown()) {
			mUsageInfoWindow.closeWindow();
			return true;
		}
		
		return false;
	}
	
	private ApplicationUsagesListFragment findUsagesFragment() {
		Fragment fragment = 
			getSupportFragmentManager().findFragmentById(
				R.id.app_usage_list_fragment);
		if (fragment instanceof ApplicationUsagesListFragment == false) {
			return null;
		}
		
		return (ApplicationUsagesListFragment)fragment;
	}

	@Override
	protected int[] listPeroidBaseFragmentIds() {
		return PEROID_FRAGMENT_IDS;
	}
	
}
