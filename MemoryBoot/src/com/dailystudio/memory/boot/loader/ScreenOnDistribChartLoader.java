package com.dailystudio.memory.boot.loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.res.Resources;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.TimeSpanUtils;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.boot.MemoryScreenOn;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.loader.DatabaseChartLoader;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class ScreenOnDistribChartLoader 
	extends DatabaseChartLoader<MemoryScreenOn> {

	private class SharedArguments {
		
		private long minDistribAvg = -1;
		private long maxDistribAvg = -1;
		
		private long minTime = -1;
		private long maxTime = -1;
		
		long[] hourDistribSums = new long[24];
		double[] hourDistribAvgs = new double[24];
		
		private void updateMinAndMaxTime(long time) {
			if (minTime == -1) {
				minTime = time;
			} else {
				if (time < minTime) {
					minTime = time;
				}
			}

			if (maxTime == -1) {
				maxTime = time;
			} else {
				if (time > maxTime) {
					maxTime = time;
				}
			}
		}
		
		private void updateMinAndMaxDistribAvg(long duration) {
			if (minDistribAvg == -1) {
				minDistribAvg = duration;
			} else {
				if (duration < minDistribAvg) {
					minDistribAvg = duration;
				}
			}

			if (maxDistribAvg == -1) {
				maxDistribAvg = duration;
			} else {
				if (duration > maxDistribAvg) {
					maxDistribAvg = duration;
				}
			}
		}
		
		@Override
		public String toString() {
			return String.format("time[%s - %s], avg[%s - %s]",
					CalendarUtils.timeToReadableString(minTime),
					CalendarUtils.timeToReadableString(maxTime),
					CalendarUtils.durationToReadableString(minDistribAvg),
					CalendarUtils.durationToReadableString(maxDistribAvg));
		}

	}

	private int[] mWeekdays = null;
	private WeakReference<ActionBarActivity> mHostActivityRef;

	public ScreenOnDistribChartLoader(Context context, 
			long start, long end, int[] weekdays) {
		super(context, start, end);
		
		if (context instanceof ActionBarActivity) {
			mHostActivityRef = new WeakReference<ActionBarActivity>(
					(ActionBarActivity)context);
		}
		
		mWeekdays = weekdays;
	}

	@Override
	protected Query getQuery(Class<MemoryScreenOn> klass) {
		Query query = super.getQuery(klass);
		
		if (mWeekdays != null && mWeekdays.length > 0) {
			List<Integer> days = new ArrayList<Integer>();
			
			for (int weekday: mWeekdays) {
				days.add(weekday);
			}
			
			ExpressionToken weekdaysSelToken = 
					MemoryScreenOn.COLUMN_TIME.WEEKDAY().inValues(
							days.toArray(new Integer[0]));
			
			ExpressionToken selToken = query.getSelection();
			if (selToken == null) {
				selToken = weekdaysSelToken;
			} else {
				selToken.and(weekdaysSelToken);
			}
			
			query.setSelection(selToken);
		}
		
		return query;
	}
	
	@Override
	protected Class<MemoryScreenOn> getObjectClass() {
		return MemoryScreenOn.class;
	}
	
	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}

	@Override
	public List<MemoryScreenOn> loadInBackground() {
		showPrompt();
		
		updateHostProgress(15);
		List<MemoryScreenOn> data = super.loadInBackground();
		updateHostProgress(100);
		
		hidePrompt();
		
		return data;
	}
	
	@Override
	protected Object buildDataset(List<MemoryScreenOn> objects,
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
		
		String seriesDuration = context.getString(
				R.string.series_title_screen_on_duration);
		
		XYSeries series = new XYSeries(seriesDuration);

		for (int i = 0; i < 24; i++) {
			args.hourDistribSums[i] = 0l;
		}
		
		MemoryScreenOn screenOn = null;
		long start = 0l;
		long end = 0l;
		for (DatabaseObject object: objects) {
			if (object instanceof MemoryScreenOn == false) {
				continue;
			}
			
			screenOn = ((MemoryScreenOn)object);

			start = screenOn.getTime();
			end = start + screenOn.getDuration();
			
			TimeSpanUtils.calculateHourDistribution(
					args.hourDistribSums, start, end);

			args.updateMinAndMaxTime(start);
		}
		
//		long days = (long)FloatMath.ceil((args.maxTime - args.minTime)
//				/ (float)CalendarUtils.DAY_IN_MILLIS);
		
		int baseProgress = 50;
		final int deltaProgress = (100 - baseProgress) / 24;
		
		long days = TimeSpanUtils.calculateDays(
				args.minTime, args.maxTime, mWeekdays);
		for (int i = 0; i < 24; i++) {
			args.hourDistribSums[i] = 
					args.hourDistribSums[i] / CalendarUtils.MINUTE_IN_MILLIS;
			
			if (days == 0) {
				args.hourDistribAvgs[i] = 0;
			} else {
				args.hourDistribAvgs[i] = 
						((double)args.hourDistribSums[i] / days);
			}
			
			series.add(i, args.hourDistribAvgs[i]);
			Logger.debug("hour = %d, sum(%d) / daysCount(%d) = average(%f) [min]", 
					i, 
					args.hourDistribSums[i],
					days,
					args.hourDistribAvgs[i]);
			
			if (args != null) {
				args.updateMinAndMaxDistribAvg((long)args.hourDistribAvgs[i]);
			}
			
			updateHostProgress(baseProgress + i * deltaProgress);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (dataset != null) {
			dataset.addSeries(series);
		}
		
		Logger.debug("sharedArguments = %s", sharedArguments);

		updateHostProgress(100);
		
		return dataset;
	}
	
	@Override
	protected Object buildRenderer(List<MemoryScreenOn> objects,
			Object sharedArguments) {
		Logger.debug("[LOADER: 0x%08x], objects.count[%d]",
				this.hashCode(), (objects == null ? 0 : objects.size()));
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
		
		XYSeriesRenderer r = new XYSeriesRenderer();
		
		r.setColor(ChartUtils.getColorResource(context, R.color.tomato_red));
		r.setPointStyle(PointStyle.DIAMOND);
		r.setFillPoints(true);
		r.setLineWidth(3);
		
//		long yMin = args.minDistribAvg;
//		long yMax = (int)Math.round(args.maxDistribAvg * 1.1);
		long yMin = 0;
		long yMax = 61;
		long xAxisMin = -1;
		long xAxisMax = 24;

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		renderer.addSeriesRenderer(r);
		
		ChartUtils.applyDefaulChartStyle(getContext(), renderer);
		
		final Resources res = context.getResources();
		if (res != null) {
			int chartMarginLeft = 20;
			int chartMarginTop = 20;
			int chartMarginRight = 20;
			int chartMarginBottom = 20;

			chartMarginLeft = res.getDimensionPixelSize(R.dimen.default_chart_margin_left);
			chartMarginTop = res.getDimensionPixelSize(R.dimen.screen_on_distrib_chart_margin_top);
			chartMarginRight = res.getDimensionPixelSize(R.dimen.default_chart_margin_right);
			chartMarginBottom = res.getDimensionPixelSize(R.dimen.default_chart_margin_bottom);
		    
			renderer.setMargins(new int[] { chartMarginTop, chartMarginLeft,
		    		chartMarginBottom, chartMarginRight });
		}
		
		final String startStr = 
				DateTimePrintUtils.printTimeStringWithoutTime(context, args.minTime);
		final String endStr = 
				DateTimePrintUtils.printTimeStringWithoutTime(context, args.maxTime);
		Logger.debug("startStr = %s", startStr);
		Logger.debug("endStr = %s", endStr);
		if (startStr != null && endStr != null) {
			if (startStr.equals(endStr)) {
				renderer.setChartTitle(startStr);
			} else {
				renderer.setChartTitle(String.format("%s - %s",
						startStr, endStr));
			}
		}
		
		renderer.setXTitle(context.getString(R.string.chart_label_hour));
		renderer.setYTitle(context.getString(R.string.chart_label_minutes));

		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		
		renderer.setXAxisMin(xAxisMin);
		renderer.setXAxisMax(xAxisMax);
		
	    renderer.setXLabels(24);
	    renderer.setYLabels(9);
	    renderer.setBarSpacing(.5);
	    
	    double panRange[] = {
		    	xAxisMin, 
		    	xAxisMax, 
		    	yMin, 
		    	yMax	
		    };
		    
		    double zoomRange[] = {
		    	xAxisMin,
			    xAxisMax, 
				yMin, 
				yMax	
		    };
			    
	    renderer.setPanLimits(panRange);
	    renderer.setZoomLimits(zoomRange);

		return renderer;
	}

	private void updateHostProgress(int progress) {
		final ActionBarActivity hostActivity = 
				getHostActivity();
		if (hostActivity != null
				&& !isAbandoned()
				&& !isReset()) {
			Logger.debug("[LOADER: 0x%08x], prg = %d, isAbandoned() = %s, isReset() = %s", 
					this.hashCode(), progress, !isAbandoned(), !isReset());
			
			hostActivity.setActionBarProgress(progress);
		}
	}
	
	private void showPrompt() {
		final ActionBarActivity hostActivity = 
				getHostActivity();
		
		if (hostActivity != null
				&& !isAbandoned()
				&& !isReset()) {
			CharSequence promptstr = 
					getContext().getString(R.string.screen_chart_load_prompt);
			
			hostActivity.showPrompt(promptstr);
		}
	}

	private void hidePrompt() {
		final ActionBarActivity hostActivity = 
				getHostActivity();
		
		if (hostActivity != null
				&& !isAbandoned()
				&& !isReset()) {
			hostActivity.hidePrompt();
		}
	}

	private ActionBarActivity getHostActivity() {
		if (mHostActivityRef == null) {
			return null;
		}
		
		return mHostActivityRef.get();
	}

}
