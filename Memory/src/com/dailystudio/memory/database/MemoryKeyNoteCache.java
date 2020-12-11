package com.dailystudio.memory.database;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.querypiece.MemoryKeyNote;

public class MemoryKeyNoteCache extends TimeCapsule {
	
	public static final Column COLUMN_CONTENT = new TextColumn("content", false);
	public static final Column COLUMN_OVER_THE_DAY_END = 
			new IntegerColumn("over_the_day_end", false);
	public static final Column COLUMN_DAY_START = 
			new LongColumn("day_start", false);
	public static final Column COLUMN_DAY_END = 
			new LongColumn("day_end", false);
	
	private final static Column[] sCloumns = {
		COLUMN_CONTENT,
		COLUMN_OVER_THE_DAY_END,
		COLUMN_DAY_START,
		COLUMN_DAY_END,
	};

	public MemoryKeyNoteCache(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public String getContent() {
		return getTextValue(COLUMN_CONTENT);
	}

	public void setContent(String content) {
		setValue(COLUMN_CONTENT, content);
	}
	
	public boolean isOverTheDayEnd() {
		return (getIntegerValue(COLUMN_OVER_THE_DAY_END) == 1);
	}

	public void setIsOverTheDayEnd(boolean overTheDayEnd) {
		setValue(COLUMN_OVER_THE_DAY_END, (overTheDayEnd ? 1 : 0));
	}
	
	public long getDayStart() {
		return getLongValue(COLUMN_DAY_START);
	}
	
	public void setDayStart(long dayStart) {
		setValue(COLUMN_DAY_START, dayStart);
	}
	
	public long getDayEnd() {
		return getLongValue(COLUMN_DAY_END);
	}
	
	public void setDayEnd(long dayEnd) {
		setValue(COLUMN_DAY_END, dayEnd);
	}

	@Override
	public String toString() {
		return String.format("%s, day[%s - %s], content = [%s], overTheDayEnd = %s",
				super.toString(),
				CalendarUtils.timeToReadableString(getDayStart()),
				CalendarUtils.timeToReadableString(getDayEnd()),
				getContent(),
				isOverTheDayEnd());
	}
	
	public static MemoryKeyNoteCache from(Context context, 
			MemoryKeyNote keynote) {
		if (context == null || keynote == null) {
			return null;
		}
		
		MemoryKeyNoteCache cache = new MemoryKeyNoteCache(context);
		cache.setTime(keynote.getTimestamp());
		cache.setContent(keynote.getContent());
		cache.setIsOverTheDayEnd(keynote.isOverTheDayEnd());
		
		return cache;
	}

}
