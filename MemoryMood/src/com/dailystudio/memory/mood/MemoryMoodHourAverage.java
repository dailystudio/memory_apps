package com.dailystudio.memory.mood;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryMoodHourAverage extends TimeCapsule {
	
	public static final Column COLUMN_HOUR = MemoryMood.COLUMN_TIME.HOUR();
	public static final Column COLUMN_MOOD_AVG = 
		new DoubleColumn(String.format("avg(%s)", MemoryMood.COLUMN_MOOD_LEVEL.getName()), 
				false);
	
	private final static Column[] sCloumns = {
		COLUMN_HOUR,
		COLUMN_MOOD_AVG,
	};

	public MemoryMoodHourAverage(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public int getHour() {
		return getIntegerValue(COLUMN_HOUR);
	}
	
	public double getMoodAverage() {
		return getDoubleValue(COLUMN_MOOD_AVG);
	}
	
	@Override
	public String toString() {
		return String.format("%s, hour: %d, avg: %f]",
				super.toString(),
				getHour(),
				getMoodAverage());
	}

}
