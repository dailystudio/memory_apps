package com.dailystudio.memory.boot;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryUptimeAverage extends TimeCapsule {
	
	public static final Column COLUMN_WEEK_TIME = MemoryBoot.COLUMN_TIME;
	public static final Column COLUMN_MOOD_AVG = 
		new DoubleColumn(String.format("avg(%s)", MemoryBoot.COLUMN_BOOT_UP_TIME.getName()), 
				false);
	
	private final static Column[] sCloumns = {
		COLUMN_WEEK_TIME,
		COLUMN_MOOD_AVG,
	};

	public MemoryUptimeAverage(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public long getWeekTime() {
		return getLongValue(COLUMN_WEEK_TIME);
	}
	
	public double getUptimeAverage() {
		return getDoubleValue(COLUMN_MOOD_AVG);
	}
	
	@Override
	public String toString() {
		return String.format("%s, week: %s, avg: %d]",
				super.toString(),
				CalendarUtils.timeToReadableString(getWeekTime()),
				getUptimeAverage());
	}

}
