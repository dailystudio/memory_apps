package com.dailystudio.memory.application.activity;

import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UselessApp;
import com.dailystudio.memory.application.ui.UselessAppInfoWindow;

public class UselessAppListActivity extends ActionBarActivity 
	implements OnListItemSelectedListener {

	
	UselessAppInfoWindow mInfoWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_useless_app_list);
		
		setupViews();
	}
	
	private void setupViews() {
		mInfoWindow = (UselessAppInfoWindow)findViewById(R.id.useless_app_info_window);
	}

	@Override
	public void onBackPressed() {
		if (hideAppInfo()) {
			return;
		}

		super.onBackPressed();
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("itemData = %s", itemData);
		if (mInfoWindow == null || itemData == null) {
			return;
		}
		
		mInfoWindow.setUselessApp((UselessApp)itemData);
		mInfoWindow.openWindow();
	}

	private boolean hideAppInfo() {
		if (mInfoWindow != null && mInfoWindow.isShown()) {
			mInfoWindow.closeWindow();
			return true;
		}
		
		return false;
	}

}
