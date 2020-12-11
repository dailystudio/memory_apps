package com.dailystudio.memory.application.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.ui.AbstractWindow;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.fragment.AndroidApplicationListFragment;
import com.dailystudio.memory.application.ui.AppInfoWindow;
import com.dailystudio.memory.application.ui.AppsFilterWindow;
import com.dailystudio.memory.application.ui.AppsSortWindow;
import com.dailystudio.nativelib.application.AndroidApplication;

public class AppsListActivity extends ActionBarActivity
	implements OnListItemSelectedListener {

	AppsFilterWindow mFilterWindow;
	AppsSortWindow mSortWindow;
	AppInfoWindow mInfoWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_application_list);
		
		setupViews();
	}
	
	private void setupViews() {
		mFilterWindow = (AppsFilterWindow)findViewById(R.id.apps_filter_window);
		mFilterWindow.addCallbacks(mFilterCallbacks);
		
		mSortWindow = (AppsSortWindow)findViewById(R.id.apps_sort_window);
		mSortWindow.addCallbacks(mSortCallbacks);
		
//		mInfoWindow = new AppInfoWindow(this);
		mInfoWindow = (AppInfoWindow)findViewById(R.id.app_info_window);
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_apps_list, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_manage_apps: {
				final AndroidApplicationListFragment fragment = 
					findAppFragment();
				
				if (fragment != null) {
					if (fragment.isManagingApps()) {
						fragment.stopManageApps();
						item.setIcon(R.drawable.ic_menu_manage_apps);
						item.setTitle(R.string.menu_manage_apps);
					} else {
						fragment.startManageApps();
						item.setIcon(R.drawable.ic_menu_list);
						item.setTitle(R.string.menu_list_apps);
					}
				}
				
				return true;
			}
			
			case R.id.menu_filter_apps: {
				showAppsFilter();
				
				return true;
			}
			
			case R.id.menu_sort_apps: {
				showAppsSort();
				
				return true;
			}
		}
		
		return super.onOverflowItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		if (hideAppsSort()) {
			return;
		} else if (hideAppsFilter()) {
			return;
		} else if (hideAppInfo()) {
			return;
		}

		super.onBackPressed();
	}

	@Override
	protected void onActionBarOverflowDisplayed() {
		super.onActionBarOverflowDisplayed();
		
		hideAppsSort();
		hideAppsFilter();
		hideAppInfo();
	}
	
	@Override
	public void onListItemSelected(Object itemData) {
		if (mInfoWindow == null || itemData == null) {
			return;
		}
		
		mInfoWindow.setApplication((AndroidApplication)itemData);
		mInfoWindow.openWindow();
	}
	
	private AndroidApplicationListFragment findAppFragment() {
		Fragment fragment = 
			getSupportFragmentManager().findFragmentById(
				R.id.apps_list_fragment);
		if (fragment instanceof AndroidApplicationListFragment == false) {
			return null;
		}
		
		return (AndroidApplicationListFragment)fragment;
	}

	private boolean hideAppInfo() {
		if (mInfoWindow != null && mInfoWindow.isShown()) {
			mInfoWindow.closeWindow();
			return true;
		}
		
		return false;
	}

	private void showAppsFilter() {
		if (mFilterWindow != null && !mFilterWindow.isShown()) {
			mFilterWindow.openWindow();
		}
	}

	private boolean hideAppsFilter() {
		if (mFilterWindow != null && mFilterWindow.isShown()) {
			mFilterWindow.closeWindow();
			return true;
		}
		
		return false;
	}
	
	private void filterApplications(int appFlags) {
		AndroidApplicationListFragment fragment = 
			findAppFragment();
		if (fragment == null) {
			return;
		}
		
		fragment.filterApps(appFlags);
	}
	
	private void showAppsSort() {
		if (mSortWindow != null && !mSortWindow.isShown()) {
			mSortWindow.openWindow();
		}
	}

	private boolean hideAppsSort() {
		if (mSortWindow != null && mSortWindow.isShown()) {
			mSortWindow.closeWindow();
			return true;
		}
		
		return false;
	}
	
	protected void sortApps(int sortType) {
		AndroidApplicationListFragment fragment = 
			findAppFragment();
		if (fragment == null) {
			return;
		}
		
		fragment.sortApps(sortType);
	}

	private AbstractWindow.Callbacks mFilterCallbacks = new AbstractWindow.Callbacks() {

		@Override
		public void onWindowOpened(AbstractWindow window) {

		}

		@Override
		public void onWindowClosed(AbstractWindow window) {
			if (mFilterWindow != null) {
				filterApplications(mFilterWindow.getAppFilterFlags());
			}
		}
		
	};

	private AbstractWindow.Callbacks mSortCallbacks = new AbstractWindow.Callbacks() {

		@Override
		public void onWindowOpened(AbstractWindow window) {

		}

		@Override
		public void onWindowClosed(AbstractWindow window) {
			if (mSortWindow != null) {
				sortApps(mSortWindow.getSortType());
			}
		}
		
	};

}