package com.dailystudio.memory.where.databaseobject;

import android.content.Context;
import android.location.Location;

import com.baidu.location.BDLocation;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class MemoryLocation extends BaseLocationObject {

	public static final Column COLUMN_DURATION = new LongColumn("duration", false);
	
	private final static Column[] sCloumns = {
		COLUMN_DURATION,
	};

	public MemoryLocation(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public void setDuration(long duration) {
		setValue(COLUMN_DURATION, duration);
	}
	
	public long getDuration() {
		return getLongValue(COLUMN_DURATION);
	}
	
	@Override
	public String toString() {
		return String.format("%s, duration(%s)",
				super.toString(),
				CalendarUtils.durationToReadableString(getDuration()));
	}
	
	public static MemoryLocation createMemoryLocation(Context context, Location loc) {
		if (context == null || loc == null) {
			return null;
		}
		
		MemoryLocation dailyloc = new MemoryLocation(context);

		dailyloc.setTime(loc.getTime());
		dailyloc.setLatitude(loc.getLatitude());
		dailyloc.setLongitude(loc.getLongitude());
		dailyloc.setAltitude(loc.getAltitude());
		dailyloc.setDuration(0);
		
		return dailyloc;
	}


	public static MemoryLocation createMemoryLocation(Context context, BDLocation loc) {
		if (context == null || loc == null) {
			return null;
		}

		MemoryLocation dailyloc = new MemoryLocation(context);

		dailyloc.setTime(System.currentTimeMillis());
		dailyloc.setLatitude(loc.getLatitude());
		dailyloc.setLongitude(loc.getLongitude());
		dailyloc.setAltitude(loc.getAltitude());
		dailyloc.setDuration(0);

		return dailyloc;
	}

}
