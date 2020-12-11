package com.dailystudio.memory.mood.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.loader.ProjectedDatabaseChartLoader;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.MemoryMoodWeekdayAverage;
import com.dailystudio.memory.mood.chart.AppMoodWeekChartBuilder;
import com.dailystudio.memory.mood.chart.BaseMoodWeekChartBuilder;

public class MoodWeekChartLoader extends ProjectedDatabaseChartLoader<MemoryMood, MemoryMoodWeekdayAverage> {

	private BaseMoodWeekChartBuilder mChartBuilder;
	
	public MoodWeekChartLoader(Context context, long weekStart, long weekEnd) {
		super(context, weekStart, weekEnd);
		
		mChartBuilder = new AppMoodWeekChartBuilder(context, weekStart, weekEnd);
	}

	@Override
	protected Query getQuery(Class<MemoryMood> klass) {
		long weekStart = getPeroidStart();
		long weekEnd = getPeroidEnd();
		
		if (weekStart <= 0 || weekEnd <= 0 || weekEnd <= weekStart) {
			final long now = System.currentTimeMillis();
			weekStart = CalendarUtils.getStartOfWeek(now);
			weekEnd = CalendarUtils.getEndOfWeek(now);
		}
		
		Logger.debug("week: %s - %s", 
				CalendarUtils.timeToReadableStringWithoutTime(weekStart),
				CalendarUtils.timeToReadableStringWithoutTime(weekEnd));
		Query query = new Query(MemoryMood.class);
		
		ExpressionToken selToken = MemoryMood.COLUMN_TIME.gte(weekStart)
			.and(MemoryMood.COLUMN_TIME.lte(weekEnd))
			.and(MemoryMood.COLUMN_MOOD_LEVEL.gt(2)
					.or(MemoryMood.COLUMN_MOOD_LEVEL.lt(-2)));
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken orderBy = MemoryMood.COLUMN_TIME.orderByDescending();
		if (orderBy != null) {
			query.setOrderBy(orderBy);
		}

		OrderingToken groupBy = MemoryMood.COLUMN_TIME.groupByDay();
		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
		
		return query;
	}
	
	@Override
	protected Class<MemoryMood> getObjectClass() {
		return MemoryMood.class;
	}
	
	@Override
	protected Class<MemoryMoodWeekdayAverage> getProjectionClass() {
		return MemoryMoodWeekdayAverage.class;
	}

	@Override
	protected Object buildDataset(List<MemoryMoodWeekdayAverage> objects,
			Object sharedArguments) {
		return mChartBuilder.buildDataset(objects, sharedArguments);
	}

	@Override
	protected Object buildRenderer(List<MemoryMoodWeekdayAverage> objects,
			Object sharedArguments) {
		
		return mChartBuilder.buildRenderer(objects, sharedArguments);
	}

}
