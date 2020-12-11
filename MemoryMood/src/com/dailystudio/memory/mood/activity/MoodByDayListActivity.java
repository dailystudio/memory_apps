package com.dailystudio.memory.mood.activity;

import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;

import android.os.Bundle;

public class MoodByDayListActivity extends MemoryPeroidBasedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mood_by_day_list);
	}

}
