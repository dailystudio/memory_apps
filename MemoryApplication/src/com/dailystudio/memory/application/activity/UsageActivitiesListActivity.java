package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.application.R;
import com.dailystudio.nativelib.application.AndroidActivity;

public class UsageActivitiesListActivity extends ActionBarActivity 
	implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_usage_activities_list);
	}

	@Override
	public void onListItemSelected(Object itemData) {
		if (itemData instanceof AndroidActivity == false) {
			return;
		}
		
		Intent i = new Intent();
		
		i.setComponent(((AndroidActivity)itemData).getComponentName());
		
		ActivityLauncher.launchActivity(this, i);
	}

}
