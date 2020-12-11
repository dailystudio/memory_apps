package com.dailystudio.memory.mood;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryMoodWeekdayAverage extends TimeCapsule {
	
	public static final Column COLUMN_WEEKDAY = MemoryMood.COLUMN_TIME.WEEKDAY();
	public static final Column COLUMN_MOOD_AVG = 
		new DoubleColumn(String.format("avg(%s)", MemoryMood.COLUMN_MOOD_LEVEL.getName()), 
				false);
	
	private final static Column[] sCloumns = {
		COLUMN_WEEKDAY,
		COLUMN_MOOD_AVG,
	};

	public MemoryMoodWeekdayAverage(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public int getWeekday() {
		return getIntegerValue(COLUMN_WEEKDAY);
	}
	
	public double getMoodAverage() {
		return getDoubleValue(COLUMN_MOOD_AVG);
	}
	
	@Override
	public String toString() {
		return String.format("%s, day: %d, avg: %d]",
				super.toString(),
				getWeekday(),
				getMoodAverage());
	}

}
