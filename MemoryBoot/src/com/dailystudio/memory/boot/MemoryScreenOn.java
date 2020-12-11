package com.dailystudio.memory.boot;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryScreenOn extends TimeCapsule {
	
	public static final Column COLUMN_BOOT_SEQUENCE = new LongColumn("bootseq", false);
	public static final Column COLUMN_DURATION = new LongColumn("duration");
	
	private final static Column[] sCloumns = {
		COLUMN_BOOT_SEQUENCE,
		COLUMN_DURATION,
	};

	public MemoryScreenOn(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public long getBootSequence() {
		return getLongValue(COLUMN_BOOT_SEQUENCE);
	}

	public void setBootSequence(long seqnum) {
		setValue(COLUMN_BOOT_SEQUENCE, seqnum);
	}
	
	public long getDuration() {
		return getLongValue(COLUMN_DURATION);
	}

	public void setDuration(long stoptime) {
		setValue(COLUMN_DURATION, stoptime);
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): seq(%d), time(%s), duration(%s))",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getBootSequence(),
				CalendarUtils.timeToReadableString(getTime()),
				CalendarUtils.durationToReadableString(getDuration()));
	}
	
}
