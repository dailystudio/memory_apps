package com.dailystudio.memory.mood.loader;

import android.content.Context;

import com.dailystudio.memory.loader.WeekdaysLoader;
import com.dailystudio.memory.mood.MemoryMood;

public class MoodWeekdaysLoader extends WeekdaysLoader<MemoryMood> {

	public MoodWeekdaysLoader(Context context) {
		super(context);
	}

	public MoodWeekdaysLoader(Context context, 
			long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<MemoryMood> getObjectClass() {
		return MemoryMood.class;
	}

}
