package com.dailystudio.memory.boot.activity;

import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;

import android.os.Bundle;

public class ScreenOnListActivity extends MemoryPeroidBasedActivity {

	private static int[] peroidFragmentIds = {
		R.id.screen_on_list_fragment,
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_screen_on_list);
	}

	@Override
	protected int[] listPeroidBaseFragmentIds() {
		return peroidFragmentIds;
	}

}
