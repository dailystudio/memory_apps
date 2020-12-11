package com.dailystudio.memory.where.activity;

import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

import android.database.Cursor;
import android.os.Bundle;

public class LocationListActivity extends AbsLocationListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_location_list);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected BaseLocationObject dumpLocationObject(Object data) {
		if (data instanceof Cursor == false) {
			return null;
		}
		
		MemoryLocation loc = new MemoryLocation(this);
		
		loc.fillValuesFromCursor((Cursor)data);
		
		return loc;
	}
	
}
