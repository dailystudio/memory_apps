package com.dailystudio.memory.where.utils;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.TimeSpanUtils;

public class TimeRange {

	private long mTimeBegin;
	private long mTimeEnd;
	private int[] mWeekdaysFilter;
	
	public TimeRange(long begin, long end, int[] filter) {
		mTimeBegin = begin;
		mTimeEnd = end;
		mWeekdaysFilter = filter;
	}
	
	public int[] calculateHourDistrib() {
		final long start = mTimeBegin;
		final long end = mTimeEnd;

		long[] hoursDistribution = 
				TimeSpanUtils.calculateHourDistribution(start, end, mWeekdaysFilter);
//		Logger.debug("LONG[%s]", hoursDistribToString(hoursDistribution));
		if (hoursDistribution == null) {
			return null;
		}
		
		int[] results = new int[24];
		for (int i = 0; i < 24; i++) {
			results[i] = (int)Math.ceil((double)hoursDistribution[i] / CalendarUtils.HOUR_IN_MILLIS);
/*			Logger.debug("[%d]: LONG[%d] / HIM[%d] = %d",
					i,
					hoursDistribution[i],
					CalendarUtils.HOUR_IN_MILLIS,
					results[i]);
*/
		}
		
		return results;
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x): range: [%s - %s], distrib: %s",
				getClass().getSimpleName(),
				hashCode(),
				CalendarUtils.timeToReadableString(mTimeBegin),
				CalendarUtils.timeToReadableString(mTimeEnd),
				hoursDistribToString(calculateHourDistrib()));
	}

	public static String hoursDistribToString(int[] hoursDistrib) {
		if (hoursDistrib == null || hoursDistrib.length < 24) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder("[ ");
		for (int i = 0; i < 24; i++) {
			builder.append(String.format("[H:%2d, %d]", i, hoursDistrib[i]));
			if (i != 23) {
				builder.append(',');
			}
		}
		
		builder.append(" ]");
		
		return builder.toString();
	}

	public static String hoursDistribToString(long[] hoursDistrib) {
		if (hoursDistrib == null || hoursDistrib.length < 24) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder("[ ");
		for (int i = 0; i < 24; i++) {
			builder.append(String.format("[H:%2d, %s]", 
					i, CalendarUtils.durationToReadableString(hoursDistrib[i])));
			if (i != 23) {
				builder.append(',');
			}
		}
		
		builder.append(" ]");
		
		return builder.toString();
	}

}
