package com.dailystudio.memory.mood.chart;

import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartBuilder;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.MemoryMoodHourAverage;
import com.dailystudio.memory.mood.Moods;
import com.dailystudio.memory.mood.R;

public abstract class BaseMoodDayChartBuilder extends ChartBuilder<MemoryMood> {

	private long mTimeOfDay;
	
	public BaseMoodDayChartBuilder(Context context, long timeOfDay) {
		super(context);
		
		if (timeOfDay <= 0) {
			timeOfDay = System.currentTimeMillis();;
		}
		
		mTimeOfDay = timeOfDay;
	}
	
	public long getTimeOfDay() {
		return mTimeOfDay;
	}

	public Object buildDataset(List<MemoryMood> objects,
			Object sharedArguments) {
		preProcessMoods(objects);
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String seriesTitleMood = context.getString(
				R.string.series_title_mood);
		String seriesTitleAvgMood = context.getString(
				R.string.series_title_avg_mood);

		TimeSeries series = new TimeSeries(seriesTitleMood);
		TimeSeries avgSeries = new TimeSeries(seriesTitleAvgMood);

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		
		dataset.addSeries(avgSeries);

		long time = 0;

		if (objects != null && objects.size() >= 0) {
			int mood = 0;
			int moodLvl = 0;
			for (MemoryMood mm: objects) {
				time = mm.getTime();
				mood = mm.getMood();
				moodLvl = Moods.getMoodLevel(mood);
	
				Logger.debug("[%s] mood = %d [lvl: %d]",
						CalendarUtils.timeToReadableString(time),
						mood,
						moodLvl);
				
				series.add(new Date(time), moodLvl);
			}

			dataset.addSeries(series);
		}
		
		SparseArray<MemoryMoodHourAverage> averages = 
				getAverageMoods(context);
		if (averages == null) {
			return dataset;
		}
		
		long baseTime = CalendarUtils.getStartOfDay(mTimeOfDay);
		
		MemoryMoodHourAverage hourAverage = null;
		double avgMoodLvl = 0.f;
		for (int hour = 0; hour < 24; hour += 4) {
			time = baseTime + hour * CalendarUtils.HOUR_IN_MILLIS;
			
			hourAverage = averages.get(hour);
/*			Logger.debug("MOOD AVG[%d, time: %s]: %s", 
					hour,
					CalendarUtils.timeToReadableString(time),
					hourAverage);
*/			
			if (hourAverage != null) {
				avgMoodLvl = hourAverage.getMoodAverage();
			}
			
			avgSeries.add(new Date(time), avgMoodLvl);
		}
		
		time = baseTime + 23 * CalendarUtils.HOUR_IN_MILLIS;
		hourAverage = averages.get(23);
/*		Logger.debug("MOOD AVG[%d, time: %s]: %s", 
				23,
				CalendarUtils.timeToReadableString(time),
				hourAverage);
*/		
		if (hourAverage != null) {
			avgMoodLvl = hourAverage.getMoodAverage();
		}
		
		avgSeries.add(new Date(time), avgMoodLvl);

		return dataset;
	}
	
	private SparseArray<MemoryMoodHourAverage> getAverageMoods(Context context) {
		if (context == null) {
			return null;
		}
		
		DatabaseConnectivity connectivity = new DatabaseConnectivity(
				context, MemoryMood.class);
				
		Query query = new Query(MemoryMood.class);
		
		ExpressionToken selToken = 
				MemoryMood.COLUMN_TIME.gte(CalendarUtils.getStartOfWeek(mTimeOfDay))
				.and(MemoryMood.COLUMN_TIME.lte(CalendarUtils.getEndOfWeek(mTimeOfDay)));
		if (selToken != null) {
//			query.setSelection(selToken);
		}

		OrderingToken groupByToken = 
				MemoryMood.COLUMN_TIME.groupByHour();
		if (groupByToken == null) {
			return null;
		}
		
		query.setGroupBy(groupByToken);
		
		List<DatabaseObject> objects = 
				connectivity.query(query, MemoryMoodHourAverage.class);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		SparseArray<MemoryMoodHourAverage> averages = 
				new SparseArray<MemoryMoodHourAverage>();
		
		MemoryMoodHourAverage hourAverage = null;
		for (DatabaseObject object: objects) {
			if (object instanceof MemoryMoodHourAverage == false) {
				continue;
			}
			
			hourAverage = (MemoryMoodHourAverage)object;
			
			averages.put(hourAverage.getHour(), hourAverage);
		}
		
		return averages;
	}

	private void preProcessMoods(List<MemoryMood> moods) {
		if (moods == null) {
			return;
		}
		
		final Context context = getContext().getApplicationContext();
		
		final long dayStart = CalendarUtils.getStartOfDay(mTimeOfDay);
		
		final int N = moods.size();
		
		if (N < 1) {
			MemoryMood mood = new MemoryMood(context);
			
			mood.setTime(dayStart);
			mood.setMood(Moods.getDefaultMoodId());
			
			moods.add(mood);
		}
		
		MemoryMood firstMood = moods.get(0);
		if (firstMood != null) {
			final long delta = 
				(firstMood.getTime() - dayStart);
			
			if (delta > CalendarUtils.HOUR_IN_MILLIS) {
				final int passHour = (int)(delta / (CalendarUtils.HOUR_IN_MILLIS * 4));
				final int firstLevel = firstMood.getMoodLevel();
				final float hourIncLevel = ((float)firstLevel / passHour);
//				Logger.debug("passHour = [%d]", passHour);
//				Logger.debug("firstLevel = [%d]", firstLevel);
//				Logger.debug("hourIncLevel = [%f]", hourIncLevel);

				MemoryMood passedMood = null;
				float passHourLevel = 0;
				int passHourMoodId = 0;
				for (int i = 0; i < passHour; i++) {
					passHourMoodId = Moods.findMoodIdByLevel((int)Math.round(passHourLevel));
//					Logger.debug("passHourLevel = %f, passHourMoodId = [%d]", 
//							passHourLevel, passHourMoodId);
					
					passedMood = new MemoryMood(context);
					
					passedMood.setTime(dayStart + i * CalendarUtils.HOUR_IN_MILLIS * 4);
					passedMood.setMood((i == 0 ? -1 : passHourMoodId));
					
					moods.add(i, passedMood);
					
					passHourLevel += hourIncLevel;
				}
			}
		}
	}
	
	@Override
	public View getChart(Object dataset, Object renderer) {
		if (dataset instanceof XYMultipleSeriesDataset == false) {
			return null;
		}
		
		if (renderer instanceof XYMultipleSeriesRenderer == false) {
			return null;
		}
		
		return  ChartFactory.getTimeChartView(
	    		getContext(), 
	    		(XYMultipleSeriesDataset)dataset, 
	    		(XYMultipleSeriesRenderer)renderer, 
	    		"HH");
	}

}
