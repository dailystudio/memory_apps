package com.dailystudio.memory.where.databaseobject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.development.Logger;

public class Hotspot extends BaseLocationObject {
    
    public static class HotspotComparator extends BaseLocationComparator<Hotspot> {

        @Override
        public int compare(Hotspot hspot1, Hotspot hspot2) {
            if (hspot1 == null) {
                return 1;
            } else if (hspot2 == null) {
                return -1;
            }
            
            final long duration1 = hspot1.getDuration();
            final long duration2 = hspot2.getDuration();
            if (duration1 != duration2) {
                return (duration1 > duration2 ? -1 : 1);
            }
            
            final long occ1 = hspot1.getOccurrence();
            final long occ2 = hspot2.getOccurrence();
            if (occ1 != occ2) {
                return (occ1 > occ2 ? -1 : 1);
            }

            return 0;
        }
        
    }
	
	private final static String TIME_SEPARATOR = ",";
	
	public static final Column COLUMN_START_TIMES = 
			new TextColumn("group_concat( " + MemoryLocation.COLUMN_TIME.getName() + " )"); 
	
	public static final Column COLUMN_END_TIMES = 
			new TextColumn("group_concat( (" 
					+ MemoryLocation.COLUMN_TIME.getName()
					+ " + "
					+ MemoryLocation.COLUMN_DURATION.getName()
					+ " ) )"); 
	
	public static final Column COLUMN_SUM_DURATION = 
		new LongColumn("sum( " + MemoryLocation.COLUMN_DURATION.getName() + " )");

	public static final Column COLUMN_OCCURRENCE = 
		new IntegerColumn("count( " + MemoryLocation.COLUMN_ID.getName() + " )");
	
	private final static Column[] sCloumns = {
		COLUMN_START_TIMES,
		COLUMN_END_TIMES,
		COLUMN_SUM_DURATION,
		COLUMN_OCCURRENCE,
	};
	
	public Hotspot(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public void setDuration(long duration) {
		setValue(COLUMN_SUM_DURATION, duration);
	}

	public long getDuration() {
		return getLongValue(COLUMN_SUM_DURATION);
	}

	public void setOccurrence(int occurrence) {
		setValue(COLUMN_OCCURRENCE, occurrence);
	}

	public int getOccurrence() {
		return getIntegerValue(COLUMN_OCCURRENCE);
	}
	
	public String getRawStartTimes() {
		return getTextValue(COLUMN_START_TIMES);
	}

	public List<Long> getStartTimes() {
		return getTimes(COLUMN_START_TIMES);
	}
	
	public void concatStartTimes(String times) {
		concatTimes(COLUMN_START_TIMES, times);
	}

	public String getRawEndTimes() {
		return getTextValue(COLUMN_END_TIMES);
	}

	public List<Long> getEndTimes() {
		return getTimes(COLUMN_END_TIMES);
	}
	
	public void concatEndTimes(String times) {
		concatTimes(COLUMN_END_TIMES, times);
	}
	
	private void concatTimes(Column column, String timesToConcat) {
		if (column == null || TextUtils.isEmpty(timesToConcat)) {
			return;
		}
		
		List<Long> concatTimes = convertTimes(timesToConcat);
		if (concatTimes == null) {
			return;
		}
		
		List<Long> oldTimes = getTimes(column);
		if (oldTimes == null) {
			setValue(column, timesToConcat);
			
			return;
		}
		
		oldTimes.addAll(concatTimes);
		
		setValue(column, timesToString(oldTimes));
	}
	
	private List<Long> getTimes(Column column) {
		return convertTimes(getTextValue(column));
	}

	private String timesToString(List<Long> times) {
		if (times == null) {
			return null;
		}
		
		final int N = times.size();
		if (N <= 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < N; i++) {
			builder.append(times.get(i));
			
			if (i != (N - 1)) {
				builder.append(TIME_SEPARATOR);
			}
		}
		
		return builder.toString();
	}
	
	public static List<Long> convertTimes(String timesString) {
		if (TextUtils.isEmpty(timesString)) {
			return null;
		}
		
		String[] timeParts = timesString.split(TIME_SEPARATOR);
		if (timeParts == null || timeParts.length <= 0) {
			return null;
		}
		
		List<Long> times = new ArrayList<Long>();
		
		long time = -1l;
		for (String timeStr: timeParts) {
			try {
				time = Long.parseLong(timeStr);
			} catch (NumberFormatException e) {
				Logger.warnning("parse time [%s] failure: %s",
						timeStr, e.toString());
				
				time = -1l;
			}
			
			if (time != -1l) {
				times.add(time);
			}
		}
		
		return times;
	}

	@Override
	public String toString() {
		return String.format("%s, start-times(%s), end-times(%s), duration(%s), occurrence(%d)",
				super.toString(),
				getStartTimes(),
				getEndTimes(),
				CalendarUtils.durationToReadableString(getDuration()),
				getOccurrence());
	}

}
