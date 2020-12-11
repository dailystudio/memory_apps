package com.dailystudio.memory.where.lifestyle;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;
import com.dailystudio.memory.person.PersonFeatures;
import com.dailystudio.memory.person.PersonInformation;
import com.dailystudio.memory.person.Persons;

public class WorkAndSpareTimeChecker extends PeroidicalAsyncChecker {
	
	public WorkAndSpareTimeChecker(Context context) {
        super(context);
    }

    @Override
    public long getCheckInterval() {
        return CalendarUtils.DAY_IN_MILLIS;
    }

    @Override
    protected void doCheck(long now, long lastTimestamp) {
    	final long end = CalendarUtils.getEndOfDay(now - CalendarUtils.DAY_IN_MILLIS);
    	final long start = end - 30 * CalendarUtils.DAY_IN_MILLIS + 1;
    	Logger.debug("check peroid: [start: %s, end: %s]",
    			CalendarUtils.timeToReadableString(start),
    			CalendarUtils.timeToReadableString(end));
    	
		TimeSpanGroup[] tsGroups = new WorktimeAnalyzer().doAnalyze(mContext, start, end);
		dumpTimeSpanGroups(tsGroups);
		if (tsGroups == null || tsGroups.length < 2) {
			return;
		}
		
		PersonInformation pi = new PersonInformation(mContext, 
				Persons.PERSON_ME);
		
		final long wamstartavg = tsGroups[0].getStartAverage();
		final long wamstartmin = tsGroups[0].getStartMinimum();
		final long wamstartmax = tsGroups[0].getStartMaximum();
		final long wamendavg = tsGroups[0].getEndAverage();
		final long wamendmin = tsGroups[0].getEndMinimum();
		final long wamendmax = tsGroups[0].getEndMaximum();

		final long wpmstartavg = tsGroups[1].getStartAverage();
		final long wpmstartmin = tsGroups[1].getStartMinimum();
		final long wpmstartmax = tsGroups[1].getStartMaximum();
		final long wpmendavg = tsGroups[1].getEndAverage();
		final long wpmendmin = tsGroups[1].getEndMinimum();
		final long wpmendmax = tsGroups[1].getEndMaximum();
		
		Map<String, String> features = new HashMap<String, String>();

		if (wamstartavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START,
					String.valueOf(wamstartavg));
		}
		
		if (wamstartmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START_EARLIEST,
					String.valueOf(wamstartmin));
		}
		
		if (wamstartmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START_LATEST,
					String.valueOf(wamstartmax));
		}
		
		if (wamendavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START,
					String.valueOf(wamendavg));
		}
	
		if (wamendmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START_EARLIEST,
					String.valueOf(wamendmin));
		}
		
		if (wamendmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START_LATEST,
					String.valueOf(wamendmax));
		}
		
		if (wpmstartavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END,
					String.valueOf(wpmstartavg));
		}
		
		if (wpmstartmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END_EARLIEST,
					String.valueOf(wpmstartmin));
		}
		
		if (wpmstartmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END_LATEST,
					String.valueOf(wpmstartmax));
		}

		if (wpmendavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END,
					String.valueOf(wpmendavg));
		}
	
		if (wpmendmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END_EARLIEST,
					String.valueOf(wpmendmin));
		}
		
		if (wpmendmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END_LATEST,
					String.valueOf(wpmendmax));
		}
		
		pi.setFeatures(features);
	}
    
    private void dumpTimeSpanGroups(TimeSpanGroup[] tsGroups) {
		if (tsGroups == null || tsGroups.length < 2) {
			Logger.debug("empty groups");
		}
		
		Logger.debug("am group: [%s]", tsGroups[0]);
		Logger.debug("pm group: [%s]", tsGroups[1]);
    }

}
