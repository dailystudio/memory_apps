package com.dailystudio.memory.boot.lifestyle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryScreenOn;
import com.dailystudio.memory.lifestyle.TimeSpan;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;

public class GetupAndSleepAnalyzer {
	
	private final static int MIN_GET_UP_HOUR = 3;

	public TimeSpanGroup doAnalyze(Context context, long start, long end) {
		if (context == null || end <= start) {
			return null;
		}
		
		List<TimeSpan> getupAndSleeps = new ArrayList<TimeSpan>();

		TimeSpan span = null;
		for (long time = start; time < end; time += CalendarUtils.DAY_IN_MILLIS) {
			span = checkGetupAndSleepTime(context, time, 
					time + CalendarUtils.DAY_IN_MILLIS + 3 * CalendarUtils.HOUR_IN_MILLIS);
			Logger.debug("%s, [%s - %s]", span, 
					CalendarUtils.timeToReadableString(time),
					CalendarUtils.timeToReadableString(time + CalendarUtils.DAY_IN_MILLIS));
			if (span == null) {
				continue;
			}

			getupAndSleeps.add(span);
		}
		
		return new TimeSpanGroup(getupAndSleeps);
	}
	
	public TimeSpan checkGetupAndSleepTime(Context context, long start, long end) {
		if (context == null || end <= start) {
			return null;
		}
		
/*		Logger.debug("check peroid: [%s - %s]",
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
*/		
		TimeCapsuleDatabaseReader<MemoryScreenOn> reader =
				new TimeCapsuleDatabaseReader<MemoryScreenOn>(context,
						MemoryScreenOn.class);
		
		List<MemoryScreenOn> data = reader.query(start, end, true);
		if (data == null) {
			return null;
		}

		final MemoryScreenOn first = getFirstValidScreenOn(data);
		if (first == null) {
			return null;
		}

		final MemoryScreenOn last = getLastValidScreenOn(data);
		if (last == null) {
			return null;
		}
		
		final TimeSpan span = new TimeSpan();
		span.start = first.getTime();
		span.end = last.getTime() + last.getDuration();
		if (span.end >= end) {
			span.end = end - 1;
		}
		
		return span;
	}
	
	private MemoryScreenOn getFirstValidScreenOn(List<MemoryScreenOn> data) {
		if (data == null) {
			return null;
		}
		
		for (MemoryScreenOn s: data) {
			if (CalendarUtils.getHour(s.getTime()) >= MIN_GET_UP_HOUR) {
				return s;
			}
		}
		
		return null;
	}
	
	private MemoryScreenOn getLastValidScreenOn(List<MemoryScreenOn> data) {
		if (data == null) {
			return null;
		}
		
		return data.get(data.size() - 1);
	}

}
