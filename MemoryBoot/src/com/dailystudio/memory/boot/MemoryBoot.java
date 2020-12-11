package com.dailystudio.memory.boot;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TimeColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryBoot extends TimeCapsule {
	
	public static final Column COLUMN_BOOT_SEQUENCE = new LongColumn("bootseq", false);
	public static final Column COLUMN_BOOT_UP_TIME = new TimeColumn("bootuptime");
	public static final Column COLUMN_BOOT_SHUTDOWN_TIME = new TimeColumn("shutdowntime");
	public static final Column COLUMN_BOOT_ESTIMATED = new IntegerColumn("estimated", false);
	
	private final static Column[] sCloumns = {
		COLUMN_BOOT_SEQUENCE,
		COLUMN_BOOT_UP_TIME,
		COLUMN_BOOT_SHUTDOWN_TIME,
		COLUMN_BOOT_ESTIMATED,
	};

	public MemoryBoot(Context context) {
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
	
	public long getBootUpTime() {
		return getLongValue(COLUMN_BOOT_UP_TIME);
	}

	public void setBootUpTime(long upTime) {
		setValue(COLUMN_BOOT_UP_TIME, upTime);
	}
	
	public long getShutDownTime() {
		return getLongValue(COLUMN_BOOT_SHUTDOWN_TIME);
	}

	public void setShutDownTime(long downTime) {
		setValue(COLUMN_BOOT_SHUTDOWN_TIME, downTime);
	}
	
	public boolean isEstimated() {
		return (getIntegerValue(COLUMN_BOOT_ESTIMATED) > 0);
	}
	
	public void setEsitmated(boolean estimated) {
		setValue(COLUMN_BOOT_ESTIMATED, 
				(estimated ? 1 : 0));
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): seq(%d), time(%s), shutdown(%s), uptime(%s))",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getBootSequence(),
				CalendarUtils.timeToReadableString(getTime()),
				CalendarUtils.timeToReadableString(getShutDownTime()),
				CalendarUtils.durationToReadableString(getBootUpTime()));
	}
	
}
