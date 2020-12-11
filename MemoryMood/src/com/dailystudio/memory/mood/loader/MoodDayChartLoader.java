package com.dailystudio.memory.mood.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.loader.DatabaseChartLoader;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.chart.AppMoodDayChartBuilder;

public class MoodDayChartLoader extends DatabaseChartLoader<MemoryMood> {

	private AppMoodDayChartBuilder mChartCore;
	
	public MoodDayChartLoader(Context context, long timeOfDay) {
		super(context);

		if (timeOfDay <= 0) {
			timeOfDay = System.currentTimeMillis();;
		}

		mChartCore = new AppMoodDayChartBuilder(context, timeOfDay);
		
		setPeroid(CalendarUtils.getStartOfDay(timeOfDay), 
				CalendarUtils.getEndOfDay(timeOfDay));
	}

	@Override
	protected Class<MemoryMood> getObjectClass() {
		return MemoryMood.class;
	}

	@Override
	protected Object buildDataset(List<MemoryMood> objects,
			Object sharedArguments) {
		return mChartCore.buildDataset(objects, sharedArguments);
	}

	@Override
	protected Object buildRenderer(List<MemoryMood> objects,
			Object sharedArguments) {
		return mChartCore.buildRenderer(objects, sharedArguments);
	}


}
