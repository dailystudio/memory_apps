package com.dailystudio.memory.where.activity;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;

import android.content.Intent;

public abstract class AbsLocationListActivity extends MemoryPeroidBasedActivity
	implements OnListItemSelectedListener {

	@Override
	public void onListItemSelected(Object itemData) {
		BaseLocationObject locObject =
				dumpLocationObject(itemData);
		Logger.debug("locObject = %s", locObject);
		if (locObject == null) {
			return;
		}
		
		String action = null;
		
		Intent i = getIntent();
		Logger.debug("i = %s", i);
        if (i != null) {
        	action = i.getAction();
        }
        
        if (Intent.ACTION_PICK.equals(action)) {
        	doPickLocation(locObject);
        } else {
        	doViewLocation(locObject);
        }
	}
	
	protected void doPickLocation(BaseLocationObject locObject) {
		Intent data = locationObjectToPickIntent(locObject);
		if (data != null) {
	        setResult(RESULT_OK, data);     
		}
        
        finish();
	}
	
	protected void doViewLocation(BaseLocationObject locObject) {
		Intent i = locationObjectToViewIntent(locObject);
		if (i == null) {
			return;
		}
		
		ActivityLauncher.launchActivity(this, i);
	}
	
	protected Intent locationObjectToPickIntent(BaseLocationObject locObject) {
		if (locObject == null) {
			return null;
		}
		
		Intent data = new Intent();
		
		data.putExtra(Constants.EXTRA_LATITUDE, locObject.getLatitude());
		data.putExtra(Constants.EXTRA_LONGITUDE, locObject.getLongitude());
		
		return data;
	}
	
	protected Intent locationObjectToViewIntent(BaseLocationObject locObject) {
		if (locObject == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), GeoPointMapActivity.class);
		
		i.putExtra(Constants.EXTRA_LATITUDE, locObject.getLatitude());
		i.putExtra(Constants.EXTRA_LONGITUDE, locObject.getLongitude());
		
		return i;
	}
	
	abstract protected BaseLocationObject dumpLocationObject(Object data);
	
}
