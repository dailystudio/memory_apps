package com.dailystudio.memory.application.loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.res.Resources;

import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.ActivityUsageStatistics;
import com.dailystudio.memory.application.databaseobject.ApplicationUsageStatistics;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.loader.ConvertedDatabaseChartLoader;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.application.R;

public class ApplicationUsageChartLoader
	extends ConvertedDatabaseChartLoader<Usage, ActivityUsageStatistics, UsageStatistics> {
	
	private static class SharedArguments {
		
		protected long minTime = 0;
		protected long maxTime = 0;
		
		protected long minDuration = 0;
		protected long maxDuration = 0;
		
		protected long sumDuration = 0;
		
		protected Set<Long> mDaySet = new HashSet<Long>();
		
		private void updateMinAndMaxTime(long time) {
			if (minTime == 0) {
				minTime = time;
			} else {
				if (time < minTime) {
					minTime = time;
				}
			}

			if (maxTime == 0) {
				maxTime = time;
			} else {
				if (time > maxTime) {
					maxTime = time;
				}
			}
		}
		
		private void updateMinAndMaxDuration(long duration) {
			if (minDuration == 0) {
				minDuration = duration;
			} else {
				if (duration < minDuration) {
					minDuration = duration;
				}
			}

			if (maxDuration == 0) {
				maxDuration = duration;
			} else {
				if (duration > maxDuration) {
					maxDuration = duration;
				}
			}
		}
		
		private void addSumDuration(long uptime) {
			sumDuration += uptime;
		}
		
		public void countDays(long time) {
			final long day = CalendarUtils.getStartOfDay(time);
			
			if (!mDaySet.contains(day)) {
				mDaySet.add(day);
			}
		}
		
		public int getDaysCount() {
			return mDaySet.size();
		}

		@Override
		public String toString() {
			return String.format("time[%s - %s], duration[%d - %d], stat[sum = %d, cnt = %d, avg = %d]",
					CalendarUtils.timeToReadableString(minTime),
					CalendarUtils.timeToReadableString(maxTime),
					minDuration, maxDuration,
					sumDuration, getDaysCount(), 
					(getDaysCount() == 0 ? 0 : (sumDuration / getDaysCount())));
		}

	}
	
	public static class StatisticsResult {
		
		private long mBeginTime;
		private long mEndTime;

		private long mDataCount;
		private long mTotalUsage;
		
		public StatisticsResult(long begin, long end) {
			mBeginTime = begin;
			mEndTime = end;
		}

		public void setDataCount(long count) {
			mDataCount = count;
		}
		
		public void setTotalUsage(long usageDuration) {
			mTotalUsage = usageDuration;
		}
		
		public long getDayCount() {
			long dayCount = (mEndTime / CalendarUtils.DAY_IN_MILLIS 
					- mBeginTime / CalendarUtils.DAY_IN_MILLIS);
			if (dayCount == 0) {
				dayCount = 1;
			}
			
			return dayCount;
		}
		
		public long getDayUseFreq() {
			final long dayCount = getDayCount();
			if (dayCount <= 0) {
				return 0l;
			}
			
			return Math.round((float)dayCount / mDataCount);
		}
		
		public long getDayUseAverage() {
			if (mDataCount <= 0) {
				return 0l;
			}
			
			return Math.round((float)mTotalUsage / mDataCount);
		}
		
		@Override
		public String toString() {
			return String.format("time[%s - %s], dayCount(%d), dayUseFreq(%d), dayUsageAvg(%s)",
					CalendarUtils.timeToReadableString(mBeginTime),
					CalendarUtils.timeToReadableString(mEndTime),
					getDayCount(),
					getDayUseFreq(), 
					CalendarUtils.durationToReadableString(getDayUseAverage()));
		}

	}

	private String mPackageName;
	
	private StatisticsResult mResult;
	
	public ApplicationUsageChartLoader(Context context, long start, long end, String pkgName) {
		super(context, start, end);
		
		mPackageName = pkgName;
	}
	
	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}
	
	@Override
	protected Object buildDataset(List<UsageStatistics> objects,
			Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		final boolean dailyChart = 
				((getPeroidEnd() - getPeroidStart()) < CalendarUtils.DAY_IN_MILLIS);
		Logger.debug("dailyChart = %s", dailyChart);
		
		XYSeries series = null;
		
		String seriesDuration = context.getString(
				R.string.series_title_usage_duration);
		
		if (dailyChart) {
			series = new XYSeries(seriesDuration);
			for (int i = 0; i < 24; i++) {
				series.add(i, .0f);
			}
		} else {
			series = new TimeSeries(seriesDuration);
		}
		
		long time = 0;
		long duration = 0;
		for (UsageStatistics us: objects) {
			if (dailyChart) {
				time = CalendarUtils.getHour(us.getTime());
				Logger.debug("hour time = %d", time);
			} else {
				time = CalendarUtils.getStartOfDay(us.getTime());
				Logger.debug("day time = %d[%s]", 
						time, CalendarUtils.timeToReadableString(time));
			}
			
			duration = us.getDurationSum() / CalendarUtils.MINUTE_IN_MILLIS;
			
			if (sharedArguments instanceof SharedArguments) {
				args.updateMinAndMaxTime(time);
				args.countDays(time);
				args.updateMinAndMaxDuration(duration);
				args.addSumDuration(duration);
			}
			
			if (duration < 0) {
				continue;
			}
			
			if (dailyChart) {
				series.add(time, duration);
				Logger.debug("DAILY ADD: %d, %d", time, duration);
			} else {
				((TimeSeries)series).add(new Date(time), duration);
			}
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (dataset != null) {
			dataset.addSeries(series);
		}
		
		if (args != null) {
			mResult = new StatisticsResult(
					getPeroidStart(), getPeroidEnd());

			mResult.setDataCount(args.getDaysCount());
			mResult.setTotalUsage(args.sumDuration * CalendarUtils.MINUTE_IN_MILLIS);
		}
		
		Logger.debug("args = %s, result = %s", args, mResult);
		
		return dataset;
	}

	@Override
	protected Object buildRenderer(List<UsageStatistics> objects,
			Object sharedArguments) {
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		XYSeriesRenderer r = new XYSeriesRenderer();
		
		r.setColor(ChartUtils.getColorResource(context, R.color.tomato_red));
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		r.setLineWidth(3);
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		renderer.addSeriesRenderer(r);

		long xMin = 0; 
		long xMax = 0;
		long yMax = 0;

		xMin = getPeroidStart();
		xMax = getPeroidEnd();
		
		if (args != null) {
			xMin = args.minTime;
			xMax = args.maxTime + 2 * CalendarUtils.DAY_IN_MILLIS;
			yMax = args.maxDuration;
		}
		
		final boolean dailyChart = 
				((getPeroidEnd() - getPeroidStart()) < CalendarUtils.DAY_IN_MILLIS);
		if (dailyChart) {
			renderer.setXAxisMin(-1);
			renderer.setXAxisMax(24);
			renderer.setYAxisMin(0);
		} else {
			renderer.setXAxisMin(xMin - CalendarUtils.DAY_IN_MILLIS / 2);
			renderer.setXAxisMax(xMax + 2 * CalendarUtils.DAY_IN_MILLIS);
			renderer.setYAxisMin(-0.5);
		}
		
		renderer.setYAxisMax((yMax <= 1) ? 10 : (yMax * 1.5));

		ChartUtils.applyDefaulChartStyle(getContext(), renderer);
	    
	    final long start = getPeroidStart();
	    final long end = getPeroidEnd();
		renderer.setChartTitle(String.format("%d/%d - %d/%d",
				CalendarUtils.getMonth(start) + 1,
				CalendarUtils.getDay(start),
				CalendarUtils.getMonth(end) + 1,
				CalendarUtils.getDay(end)));

		final Resources res = context.getResources();
		if (res != null) {
			int chartMarginLeft = 20;
			int chartMarginTop = 20;
			int chartMarginRight = 20;
			int chartMarginBottom = 20;

			chartMarginLeft = res.getDimensionPixelSize(R.dimen.app_usage_chart_hmargin);
			chartMarginTop = res.getDimensionPixelSize(R.dimen.app_usage_chart_topmargin);
			chartMarginRight = res.getDimensionPixelSize(R.dimen.app_usage_chart_hmargin);
			chartMarginBottom = res.getDimensionPixelSize(R.dimen.app_usage_chart_vmargin);
		    
			renderer.setMargins(new int[] { chartMarginTop, chartMarginLeft,
		    		chartMarginBottom, chartMarginRight });
		}
		
		if (dailyChart) {
			renderer.setBarSpacing(0.5);
		    renderer.setXLabels(24);
		} else {
			renderer.setXLabels(8);
		}
		renderer.setYLabels(5);

		renderer.setShowLegend(false);
//		renderer.setShowLabels(false);

	    double panRange[] = {
	    	xMin, 
	    	xMax, 
	    	(dailyChart ? 0 : -0.5), 
	    	yMax	
	    };
	    
	    double zoomRange[] = {
	    	xMin, 
		    xMax, 
		    (dailyChart ? 0 : -0.5), 
		    yMax	
	    };
		    
	    if (dailyChart) {
	    	renderer.setPanEnabled(false);
		    renderer.setZoomEnabled(false);
	    }
	    
	    renderer.setPanLimits(panRange);
	    renderer.setZoomLimits(zoomRange);

		return renderer;
	}

	@Override
	protected Class<Usage> getObjectClass() {
		return Usage.class;
	}
	
	@Override
	protected Class<ActivityUsageStatistics> getProjectionClass() {
		return ActivityUsageStatistics.class;
	}
	
	@Override
	protected Query getQuery(Class<Usage> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		Logger.debug("loader[%s], peroid = [%s - %s]", 
				this,
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		Query query = null;
		
		if (end <= start) {
			query = super.getQuery(getObjectClass());
		} else {
			TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(getObjectClass());
			
			query = builer.getQueryForIntersect(
					Usage.COLUMN_TIME, 
					Usage.COLUMN_DURATION,
					start, end);
		}
		
		final boolean dailyChart = 
				((getPeroidEnd() - getPeroidStart()) < CalendarUtils.DAY_IN_MILLIS);
		Logger.debug("dailyChart = %s", dailyChart);
		
		OrderingToken groupBy = null;

		if (dailyChart) {
			groupBy = Usage.COLUMN_TIME.groupByHour();
		} else {
			groupBy = Usage.COLUMN_TIME.groupByDay();
		}

		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
		
		OrderingToken orderByToken = ActivityUsageStatistics.COLUMN_TIME.orderByAscending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		final Context context = getContext();
		
		Integer[] compIds = ArrayUtils.toIntegerArray(
				UsageComponentDatabaseModal.getComponentIds(context, 
						mPackageName));
		if (compIds == null || compIds.length <= 0) {
			return query;
		}

		ExpressionToken selCompIdsToken = 
			Usage.COLUMN_COMPONENT_ID.inValues(compIds);
		
		if (selCompIdsToken != null) {
			ExpressionToken selToken = query.getSelection();

			if (selToken == null) {
				selToken = selCompIdsToken;
			} else {
				selToken = selToken.and(selCompIdsToken);
			}
			
			query.setSelection(selToken);
		}
		
		Logger.debug("query = %s", query);
		
		return query;
	}
	
	@Override
	protected List<UsageStatistics> onLoadInBackground() {
		mResult = null;
		
		if (mPackageName == null) {
			return null;
		}

		final List<UsageStatistics> actstats =
			super.onLoadInBackground();

		return convertToApplicationUsageStatistics(actstats);
	}
	
	private List<UsageStatistics> convertToApplicationUsageStatistics(
			List<UsageStatistics> actstats) {
		if (actstats == null || actstats.size() <= 0) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		List<UsageStatistics> appstats =
			new ArrayList<UsageStatistics>();
		
		ActivityUsageStatistics actus = null;
		ApplicationUsageStatistics appus = null;
		for (UsageStatistics us: actstats) {
			if (us instanceof ActivityUsageStatistics == false) {
				continue;
			}
			
			actus = (ActivityUsageStatistics)us;
			
			appus = new ApplicationUsageStatistics(context);
			appus.setTime(actus.getTime());
			appus.setPackageName(mPackageName);
			appus.setDurationSum(actus.getDurationSum());
			
			appstats.add(appus);
			Logger.debug("appus = %s", appus);
		}
		
		return appstats;
	}
	
	public StatisticsResult getResult() {
		return mResult;
	}
	
}
