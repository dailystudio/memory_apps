package com.dailystudio.memory.application.activity;

import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.R;

public class FavoriteAppsListActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_favorite_apps_list);
	}

	@Override
	public void onListItemSelected(Object itemData) {
		Logger.debug("object = %s", itemData);
	}
	
}
