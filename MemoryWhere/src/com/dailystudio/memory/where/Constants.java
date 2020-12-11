package com.dailystudio.memory.where;

import com.dailystudio.datetime.CalendarUtils;

public class Constants extends com.dailystudio.memory.Constants {

	public static final String EXTRA_LATITUDE = "memory.intent.EXTRA_LATITUDE";
	public static final String EXTRA_LONGITUDE = "memory.intent.EXTRA_LONGITUDE";

	public static final String EXTRA_START_TIMES = "memory.intent.EXTRA_START_TIMES";
	public static final String EXTRA_END_TIMES = "memory.intent.EXTRA_END_TIMES";
    public static final String EXTRA_IDSPOT_DISTRIB_BITS = "memory.intent.EXTRA_IDSPOT_DISTRIB_BITS";
    public static final String EXTRA_SAMPLE_DISTRIB_BITS = "memory.intent.EXTRA_SAMPLE_DISTRIB_BITS";
	public static final String EXTRA_IDSPOT_ID = "memory.intent.EXTRA_IDSPOT_ID";

	public static final int HOTSPOT_PRE_CANDIDATE_LIMIT = 200;
	public static final long HOTSPOT_PRE_CANDIDATE_TIME_SPAN = (CalendarUtils.DAY_IN_MILLIS * 30);
	public static final int HOTSPOT_CANDIDATE_LIMIT = 10;
	public static final long HOTSPOT_DURATION_THRESHOLD = (CalendarUtils.HOUR_IN_MILLIS * 2);

	public static final long LOCATION_REQUEST_THRESHOLD = 20;
	public static final long LOCATION_REQUEST_MIN_INTERVAL = 30 * CalendarUtils.SECOND_IN_MILLIS;
	public static final int NEARY_BY_THRESHOLD = 130;
	public static final int IDSPOT_NEARY_BY_THRESHOLD = 200;

}
