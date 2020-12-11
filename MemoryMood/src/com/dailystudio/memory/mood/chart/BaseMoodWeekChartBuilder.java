package com.dailystudio.memory.mood.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.view.View;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartBuilder;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.MemoryMoodWeekdayAverage;
import com.dailystudio.memory.mood.R;

public abstract class BaseMoodWeekChartBuilder extends ChartBuilder<MemoryMoodWeekdayAverage> {

	private long mPeroidStart;
	private long mPeroidEnd;
	
	public BaseMoodWeekChartBuilder(Context context) {
		this(context, -1, -1);
	}

	public BaseMoodWeekChartBuilder(Context context, long start, long end) {
		super(context);
		
		setPeroid(start, end);
	}

	public void setPeroid(long start, long end) {
		mPeroidStart = start;
		mPeroidEnd = end;
		
		if (mPeroidStart < 0) {
			mPeroidStart = System.currentTimeMillis();
		}
		
		if (mPeroidEnd < mPeroidStart) {
			mPeroidEnd = mPeroidStart;
		}
	}
	
	public long getPeroidStart() {
		return mPeroidStart;
	}
	
	public long getPeroidEnd() {
		return mPeroidEnd;
	}
	
	@Override
	public Object buildDataset(List<MemoryMoodWeekdayAverage> objects, Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String seriesTitleMood = context.getString(
				R.string.series_title_mood);

		String seriesTitleAvgMood = context.getString(
				R.string.series_title_avg_mood);

		XYSeries series = new XYSeries(seriesTitleMood);
		XYSeries avgSeries = new XYSeries(seriesTitleAvgMood);

		double[] avergaes = new double[7];
		double[] weekdayAverages = new double[7];
		for (int i = 0; i < 7; i++) {
			avergaes[i] = .0;
			weekdayAverages[i] = .0;
		}
		
		final long now = System.currentTimeMillis();
		
		long weekStart = getPeroidStart();
		long weekEnd = getPeroidEnd();
		int weekday = 7;
		
		if (weekStart <= 0 || weekEnd <= 0 || weekEnd <= weekStart) {
			weekStart = CalendarUtils.getStartOfWeek(now);
			weekEnd = CalendarUtils.getEndOfWeek(now);
		}
		
		if (weekEnd > now) {
			weekday = CalendarUtils.getWeekDay(now);
		}
		
		Logger.debug("weekday = %d", weekday);
		
		MemoryMoodWeekdayAverage feelAvg = null;
		int day = 0;
		double average = 0;
		for (DatabaseObject object: objects) {
			if (object instanceof MemoryMoodWeekdayAverage == false) {
				continue;
			}
			
			feelAvg = ((MemoryMoodWeekdayAverage)object);

			day = feelAvg.getWeekday();
			average = feelAvg.getMoodAverage();

			day = (day == 0 ? 6: day - 1);

			Logger.debug("day = %d, average = %f", day, average);
			
			avergaes[day] = average;
		}
		
		for (int i = 0; i < 7; i++) {
			if ((i + 1) <= weekday) {
				series.add(i + 1, avergaes[i]);
			}
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (dataset != null) {
			dataset.addSeries(avgSeries);
			dataset.addSeries(series);
		}

		List<MemoryMoodWeekdayAverage> prevAverages = 
				getAverageMoods(context);
		if (prevAverages == null) {
			return dataset;
		}

		for (MemoryMoodWeekdayAverage weekdayAvg: prevAverages) {
			day = weekdayAvg.getWeekday();
			average = weekdayAvg.getMoodAverage();

			day = (day == 0 ? 6: day - 1);

			Logger.debug("day = %d, weekdayAverages = %f", day, average);
			
			weekdayAverages[day] = average;
		}
		
		for (int i = 0; i < 7; i++) {
			avgSeries.add(i + 1, weekdayAverages[i]);
		}
		
		return dataset;
	}

	private List<MemoryMoodWeekdayAverage> getAverageMoods(Context context) {
		if (context == null) {
			return null;
		}
		
		DatabaseConnectivity connectivity = new DatabaseConnectivity(
				context, MemoryMood.class);
				
		Query query = new Query(MemoryMood.class);
		
		OrderingToken groupByToken = 
				MemoryMood.COLUMN_TIME.groupByWeekday();
		if (groupByToken == null) {
			return null;
		}
		
		query.setGroupBy(groupByToken);
		
		List<DatabaseObject> objects = 
				connectivity.query(query, MemoryMoodWeekdayAverage.class);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		List<MemoryMoodWeekdayAverage> averages = 
				new ArrayList<MemoryMoodWeekdayAverage>();
		
		MemoryMoodWeekdayAverage weekdayAverage = null;
		for (DatabaseObject object: objects) {
			if (object instanceof MemoryMoodWeekdayAverage == false) {
				continue;
			}
			
			weekdayAverage = (MemoryMoodWeekdayAverage)object;
			
			averages.add(weekdayAverage);
		}
		
		return averages;
	}

	@Override
	public View getChart(Object dataset, Object renderer) {
		if (dataset instanceof XYMultipleSeriesDataset == false) {
			return null;
		}
		
		if (renderer instanceof XYMultipleSeriesRenderer == false) {
			return null;
		}
		
		return ChartFactory.getLineChartView(
				getContext(), 
				(XYMultipleSeriesDataset)dataset, 
				(XYMultipleSeriesRenderer)renderer);
	}
	
}
