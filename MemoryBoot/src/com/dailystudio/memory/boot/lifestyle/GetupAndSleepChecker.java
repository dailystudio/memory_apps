package com.dailystudio.memory.boot.lifestyle;

import java.util.HashMap;
import java.util.Map;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;
import com.dailystudio.memory.person.PersonFeatures;
import com.dailystudio.memory.person.PersonInformation;
import com.dailystudio.memory.person.Persons;

import android.content.Context;

public class GetupAndSleepChecker extends PeroidicalAsyncChecker {
	
	public GetupAndSleepChecker(Context context) {
		super(context);
	}
	
	@Override
	public long getCheckInterval() {
		return CalendarUtils.DAY_IN_MILLIS;
	}

	@Override
	protected void doCheck(long now, long lastTimestamp) {
		final long end = CalendarUtils.getStartOfDay(System.currentTimeMillis());
		final long start = end - 30 * CalendarUtils.DAY_IN_MILLIS;

		TimeSpanGroup summary = new GetupAndSleepAnalyzer().doAnalyze(
				mContext, start, end);
		
		Logger.debug("summary = %s", summary);
		if (summary == null) {
			return;
		}
		
		PersonInformation pi = new PersonInformation(mContext, 
				Persons.PERSON_ME);
		
		final long getupavg = summary.getStartAverage();
		final long sleepavg = summary.getEndAverage();
		final long getupmin = summary.getStartMinimum();
		final long getupmax = summary.getStartMaximum();
		final long sleepmin = summary.getEndMinimum();
		final long sleepmax = summary.getEndMaximum();
		
		Map<String, String> features = new HashMap<String, String>();
		if (getupavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_GETUP,
					String.valueOf(getupavg));
		}
		
		if (sleepavg > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_SLEEP,
					String.valueOf(sleepavg));
		}
		
		if (getupmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_GETUP_EARLIEST,
					String.valueOf(getupmin));
		}
		
		if (getupmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_SLEEP_EARLIEST,
					String.valueOf(getupmax));
		}
		
		if (sleepmin > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_GETUP_LATEST,
					String.valueOf(sleepmin));
		}
		
		if (sleepmax > 0) {
			features.put(PersonFeatures.FEATURE_LIFE_STYLE_SLEEP_LATEST,
					String.valueOf(sleepmax));
		}
		
		pi.setFeatures(features);
	}

}
