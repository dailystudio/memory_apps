package com.dailystudio.memory.mood.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.dataobject.WeekdayObject;
import com.dailystudio.memory.fragment.AbsWeekdaysListFragment;
import com.dailystudio.memory.mood.LoaderIds;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.loader.MoodWeekdaysLoader;

public class WeekdaysListFragment extends AbsWeekdaysListFragment<MemoryMood> {
	
	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_WEEKDAYS_LOADER;
	}

	@Override
	public Loader<List<WeekdayObject>> onCreateLoader(int arg0, Bundle arg1) {
		final long now = System.currentTimeMillis();
		
		long start = getPeroidStart();
		if (start <= 0) {
			start = CalendarUtils.getStartOfWeek(now);
		}
		
		long end = getPeroidEnd();
		if (end <= 0) {
			end = CalendarUtils.getEndOfWeek(now);
		}
		
		return new MoodWeekdaysLoader(getActivity(), start, end);
	}

}
