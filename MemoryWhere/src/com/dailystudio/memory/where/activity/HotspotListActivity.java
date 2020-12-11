package com.dailystudio.memory.where.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.Hotspot;

public class HotspotListActivity extends AbsLocationListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hotspot_list);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected BaseLocationObject dumpLocationObject(Object data) {
		if (data instanceof Hotspot == false) {
			return null;
		}
		
		return (Hotspot)data;
	}
	
	@Override
	protected Intent locationObjectToViewIntent(BaseLocationObject locObject) {
		if (locObject instanceof Hotspot == false) {
			return super.locationObjectToViewIntent(locObject);
		}
		
		Intent i = super.locationObjectToViewIntent(locObject);
		if (i == null) {
			return i;
		}
		
		i.setClass(getApplicationContext(), HotspotMapActivity.class);

		Hotspot hotspot = (Hotspot)locObject;
		
		String startTimes = hotspot.getRawStartTimes();
		String endTimes = hotspot.getRawEndTimes();
		
		i.putExtra(Constants.EXTRA_START_TIMES, startTimes);
		i.putExtra(Constants.EXTRA_END_TIMES, endTimes);
		
		return i;
	}
	
}
