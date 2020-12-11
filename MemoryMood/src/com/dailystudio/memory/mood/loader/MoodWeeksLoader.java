package com.dailystudio.memory.mood.loader;

import android.content.Context;

import com.dailystudio.memory.loader.WeeksLoader;
import com.dailystudio.memory.mood.MemoryMood;

public class MoodWeeksLoader extends WeeksLoader<MemoryMood> {

	public MoodWeeksLoader(Context context) {
		super(context);
	}

	public MoodWeeksLoader(Context context, 
			long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<MemoryMood> getObjectClass() {
		return MemoryMood.class;
	}

}
