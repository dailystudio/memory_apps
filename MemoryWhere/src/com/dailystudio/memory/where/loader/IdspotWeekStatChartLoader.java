package com.dailystudio.memory.where.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.TimeSpanUtils;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.loader.DatabaseChartLoader;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;
import com.dailystudio.memory.where.hotspot.IdentifiedHotspotColors;

public class IdspotWeekStatChartLoader extends DatabaseChartLoader<IdspotHistory> {
	
	private final static int WEEKDAYS = 7;
	
	private class SharedArguments {
		
		Map<HotspotIdentity, List<Long>> mIdspotDurationsWeekdaysMap = 
				new HashMap<HotspotIdentity, List<Long>>();
		
		private List<Long> createWeekdayList() {
			List<Long> wdayDurationList = new ArrayList<Long>(WEEKDAYS);
			
			for (int i = 0; i < WEEKDAYS; i++) {
				wdayDurationList.add(0l);
			}
			
			return wdayDurationList;
		}

		private void calculateDuration(
				long dayStart,
				long dayEnd,
				IdspotHistory history) {
			if (history == null) {
				return;
			}
			
			int weekday = CalendarUtils.getWeekDay(dayStart);
			
			weekday = (weekday == 0 ? 6: weekday - 1);
			if (weekday < 0 || weekday >= WEEKDAYS) {
				return;
			}
			
			final HotspotIdentity identity = 
					history.getIdentity();
			if (identity == null) {
				return;
			}
			
			List<Long> wdayDurationList = mIdspotDurationsWeekdaysMap.get(identity);
			if (wdayDurationList == null) {
				wdayDurationList = createWeekdayList();
				
				mIdspotDurationsWeekdaysMap.put(identity, wdayDurationList);
			}
			
			long duration = history.getDuration();
			long endTime = 0;
			
			if (duration == 0 
					&& IdspotHistoryDatabaseModal.isLastHistory(
							getContext(), history)) {
				endTime = System.currentTimeMillis(); 
			} else {
				endTime = history.getTime() + history.getDuration();
			}
			
			duration = Math.round(TimeSpanUtils.calculateOverlapDuration(
						history.getTime(), 
						endTime,
						dayStart,
						dayEnd) / (double)CalendarUtils.HOUR_IN_MILLIS);
			
			long oldDuration = wdayDurationList.get(weekday);
			
/*			Logger.debug("[idspotId = %d], history: %s, overlap[%s - %s] NEW DURATION[%d]: old[%d] + duration[%d]", 
					mIdspotId,
					history,
					CalendarUtils.timeToReadableString(dayStart),
					CalendarUtils.timeToReadableString(dayEnd),
					(oldDuration + duration),
					oldDuration,
					duration);
*/
			wdayDurationList.set(weekday, (oldDuration + duration));
		}
		
		@Override
		public String toString() {
			return String.format("mSumDurations = %s", 
					mIdspotDurationsWeekdaysMap);
		}

	}
	
	private int mMaxWeekday = WEEKDAYS - 1;
	private int mIdspotId = -1;
	
	public IdspotWeekStatChartLoader(Context context, 
			long start, long end, int idspotId) {
		super(context, start, end);
		Logger.debug("[%s - %s]", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		final boolean currentWeek = CalendarUtils.isCurrentWeek(start);
		if (currentWeek) {
			final long now = System.currentTimeMillis();
			int weekday = CalendarUtils.getWeekDay(now);
			
			mMaxWeekday = (weekday == 0 ? 6: weekday - 1);
		}
		
		mIdspotId = idspotId;
	}

	@Override
	protected Class<IdspotHistory> getObjectClass() {
		return IdspotHistory.class;
	}
	
	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}
	
	@Override
	protected Query getQuery(Class<IdspotHistory> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		
		Query query = null;
		
		if (end <= start) {
			query = super.getQuery(getObjectClass());
		} else {
			TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(getObjectClass());
			
			query = builer.getQueryForIntersect(
					IdspotHistory.COLUMN_TIME, 
					IdspotHistory.COLUMN_DURATION,
					start, end);
		}
		
		if (mIdspotId <= 0) {
			return query;
		}
		
		ExpressionToken idspotSelToken = 
				IdspotHistory.COLUMN_IDSPOT_ID.eq(mIdspotId);
		if (idspotSelToken == null) {
			return query;
		}
		
		ExpressionToken selToken = query.getSelection();
		if (selToken == null) {
			selToken = idspotSelToken;
		} else {
			selToken.and(idspotSelToken);
		}
		
		query.setSelection(selToken);

		return query;
	}

	
	@Override
	protected Object buildDataset(List<IdspotHistory> objects,
			Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		if (args == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		final int N = objects.size();
		if (N <= 0) {
			return null;
		}
		
		final long weekStart = CalendarUtils.getStartOfWeek(getPeroidStart());
		final long weekEnd = CalendarUtils.getEndOfWeek(getPeroidEnd());
		Logger.debug("[idspotId = %d], range[%s - %s], week[%s - %s]", 
				mIdspotId, 
				CalendarUtils.timeToReadableString(getPeroidStart()),
				CalendarUtils.timeToReadableString(getPeroidEnd()),
				CalendarUtils.timeToReadableString(weekStart),
				CalendarUtils.timeToReadableString(weekEnd));
		long time = 0;
		DatabaseObject object;
		IdspotHistory history = null;
		int i;
		for (time = weekStart; time <= weekEnd; time += CalendarUtils.DAY_IN_MILLIS) {
			
			for (i = 0; i < N; i++) {
				object = objects.get(i);
				if (object instanceof IdspotHistory == false) {
					continue;
				}
				
				history = ((IdspotHistory)object);
	
				args.calculateDuration(
						CalendarUtils.getStartOfDay(time), 
						CalendarUtils.getEndOfDay(time),
						history);
			}
		}
		
		if (args.mIdspotDurationsWeekdaysMap == null ||
				args.mIdspotDurationsWeekdaysMap.size() <= 0) {
			return null;
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		
		HotspotIdentityInfo hInfo = null;
		String seriesTitle = null;
		List<Long> weedayDurations = null;
		XYSeries series = null;
		
		List<HotspotIdentity> sortedIdentities = 
				sortIdentities(args.mIdspotDurationsWeekdaysMap.keySet());
	    if (sortedIdentities == null) {
	    	return null;
	    }
		
		for (HotspotIdentity identity: sortedIdentities) {
			hInfo = HotspotIdentifier.getIdentityInfo(identity);
			if (hInfo == null) {
				args.mIdspotDurationsWeekdaysMap.remove(identity);
				continue;
			}
			
			weedayDurations = args.mIdspotDurationsWeekdaysMap.get(identity);
			
			seriesTitle = context.getString(hInfo.labelResId);
			
			series = new XYSeries(seriesTitle);
			for (i = 0; i < WEEKDAYS; i++) {
				if (i > mMaxWeekday) {
					continue;
				}
				series.add(i + 1, weedayDurations.get(i));
			}

			dataset.addSeries(series);
		}

		Logger.debug("sharedArguments = %s, dataset = %s", 
				sharedArguments,
				dataset);

		return dataset;
	}
	
	@Override
	protected Object buildRenderer(List<IdspotHistory> objects,
			Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		if (args == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		if (args.mIdspotDurationsWeekdaysMap == null ||
				args.mIdspotDurationsWeekdaysMap.size() <= 0) {
			return null;
		}
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		ChartUtils.applyDefaulChartStyle(context, renderer);
	    
		List<HotspotIdentity> sortedIdentities = 
				sortIdentities(args.mIdspotDurationsWeekdaysMap.keySet());
	    if (sortedIdentities == null) {
	    	return null;
	    }
		
		XYSeriesRenderer r = null;
	    int colorResId = 0;
		for (HotspotIdentity identity: sortedIdentities) {
			r = new XYSeriesRenderer();
			
			colorResId = IdentifiedHotspotColors.getColorOfHotspotIdentity(
					identity);
			
			r.setColor(ChartUtils.getColorResource(context, colorResId));
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
			r.setLineWidth(3);
			
			renderer.addSeriesRenderer(r);
	    }
		
		renderer.setXTitle(context.getString(R.string.chart_label_weekdays));
		renderer.setYTitle(context.getString(R.string.chart_label_hours));

		renderer.setYAxisMin(-0.5);
		renderer.setYAxisMax(24.5);
		
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(7.5);
		
	    renderer.setXLabels(7);
	    renderer.setYLabels(12);

	    renderer.setPanEnabled(false, false);
	    renderer.setPanLimits(new double[] { 
	    		0, 7, 
	    		0,
	    		24});

		return renderer;
	}
	
	private List<HotspotIdentity> sortIdentities(Set<HotspotIdentity> identities) {
		if (identities == null) {
			return null;
		}
		
		List<HotspotIdentity> list = new ArrayList<HotspotIdentity>();
		for (HotspotIdentity identity: identities) {
			list.add(identity);
		}
		
		Collections.sort(list);
		
		return list;
	}

}
