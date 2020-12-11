package com.dailystudio.memory.application.loader;

import android.content.Context;

import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.loader.WeeksLoader;

public class UsageWeeksLoader extends WeeksLoader<Usage> {

	public UsageWeeksLoader(Context context) {
		super(context);
	}
	
	public UsageWeeksLoader(Context context, 
			long start, long end) {
		super(context, start, end);
	}

	@Override
	protected Class<Usage> getObjectClass() {
		return Usage.class;
	}

}
