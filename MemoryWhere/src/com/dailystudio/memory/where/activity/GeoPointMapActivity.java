package com.dailystudio.memory.where.activity;

import com.dailystudio.memory.where.R;

import android.os.Bundle;

public class GeoPointMapActivity extends BaseGeoPointMapActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_geo_point_map);
	}

}
