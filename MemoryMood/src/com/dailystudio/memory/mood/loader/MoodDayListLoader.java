package com.dailystudio.memory.mood.loader;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.memory.loader.PeroidDatabaseCursorLoader;
import com.dailystudio.memory.mood.MemoryMood;

public class MoodDayListLoader extends PeroidDatabaseCursorLoader {

	private long mTimeOfDay;
	
	public MoodDayListLoader(Context context, long timeOfDay) {
		super(context);

		if (timeOfDay <= 0) {
			timeOfDay = System.currentTimeMillis();;
		}
		
		mTimeOfDay = timeOfDay;
		
		setPeroid(CalendarUtils.getStartOfDay(mTimeOfDay), 
				CalendarUtils.getEndOfDay(mTimeOfDay));
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryMood.class;
	}

}
