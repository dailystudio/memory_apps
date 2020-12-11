package com.dailystudio.memory.database.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.lifestyle.TimeSpan;

public class KeynotesPagesLoader extends AbsAsyncDataLoader<List<TimeSpan>> {

	private final int N_DAYS = 3;
	
	public KeynotesPagesLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<TimeSpan> loadInBackground() {
		List<TimeSpan> timespans = new ArrayList<TimeSpan>();
		
		final long now = System.currentTimeMillis();
		
		long time = 0;
		long start = 0;
		long end = 0;
		for (int i = 0; i < N_DAYS; i++) {
			time = now - i * CalendarUtils.DAY_IN_MILLIS;
			
			start = CalendarUtils.getStartOfDay(time);
			end = CalendarUtils.getEndOfDay(time);
			
			timespans.add(new TimeSpan(start, end));
		}
		
		Logger.debug("keynotes spnas: [%s]", timespans);
	
		return timespans;
	}

}
