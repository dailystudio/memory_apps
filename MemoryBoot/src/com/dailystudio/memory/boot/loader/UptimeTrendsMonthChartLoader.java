package com.dailystudio.memory.boot.loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.res.Resources;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.loader.DatabaseChartLoader;
import com.dailystudio.memory.chart.ChartUtils;

public class UptimeTrendsMonthChartLoader extends DatabaseChartLoader<MemoryBoot> {

	private class SharedArguments {
		
		private long minTime = 0;
		private long maxTime = 0;
		
		private long minUptime = 0;
		private long maxUptime = 0;
		
		private long sumUptime = 0;
		private long cntUptime = 0;
		
		private List<Long> dayStarts = new ArrayList<Long>();
		private HashMap<Long, Long> daySumUptime = new HashMap<Long, Long>();
		private HashMap<Long, Long> dayCntUptime = new HashMap<Long, Long>();
		
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
		
		private void updateMinAndMaxUptime(long uptime) {
			if (minUptime == 0) {
				minUptime = uptime;
			} else {
				if (uptime < minUptime) {
					minUptime = uptime;
				}
			}

			if (maxUptime == 0) {
				maxUptime = uptime;
			} else {
				if (uptime > maxUptime) {
					maxUptime = uptime;
				}
			}
		}
		
		private void addSumUptime(long uptime) {
			sumUptime += uptime;
			cntUptime++;
		}
		
		public void groupInDay(long time, long uptime) {
			final long dayStart = CalendarUtils.getStartOfDay(time);
			
			if (daySumUptime.containsKey(dayStart)) {
				Long oldSum = daySumUptime.get(dayStart);
				
				daySumUptime.put(dayStart, (oldSum.longValue() + uptime));
			} else {
				dayStarts.add(dayStart);
				daySumUptime.put(dayStart, uptime);
			}
			
			if (dayCntUptime.containsKey(dayStart)) {
				Long oldCnt = dayCntUptime.get(dayStart);
				
				dayCntUptime.put(dayStart, (oldCnt.longValue() + 1));
			} else {
				dayCntUptime.put(dayStart, 1l);
			}
		}
		
		@Override
		public String toString() {
			return String.format("time[%s - %s], uptime[%d - %d], stat[sum = %d, cnt = %d, avg = %d], daySumUptime = %s, dayCntUptime = %s",
					CalendarUtils.timeToReadableString(minTime),
					CalendarUtils.timeToReadableString(maxTime),
					minUptime, maxUptime,
					sumUptime, cntUptime, 
					(cntUptime == 0 ? 0 : (sumUptime / cntUptime)),
					daySumUptime,
					dayCntUptime);
		}

	}
	
	public UptimeTrendsMonthChartLoader(Context context, 
			long monthStart, long monthEnd) {
		super(context, monthStart, monthEnd);
	}

	@Override
	protected Query getQuery(Class<MemoryBoot> klass) {
		TimeCapsuleQueryBuilder builder = new TimeCapsuleQueryBuilder(getObjectClass());
		
		long monthStart = getPeroidStart();
		long monthEnd = getPeroidEnd();
		
		if (monthStart <= 0 || monthEnd <= 0 || monthEnd <= monthStart) {
			final long now = System.currentTimeMillis();
			monthStart = CalendarUtils.getStartOfMonth(now);
			monthEnd = CalendarUtils.getEndOfMonth(now);
			
			setPeroid(monthStart, monthEnd);
		}
		
		Query query = builder.getQuery(monthStart, monthEnd);
		
		ExpressionToken selToken = query.getSelection();
		
		if (selToken == null) {
			selToken = MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1);
		} else {
			selToken = selToken.and(MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1));
		}
			
		if (selToken != null) {
			query.setSelection(selToken);
		}

		return query;
	}
	
	@Override
	protected Class<MemoryBoot> getObjectClass() {
		return MemoryBoot.class;
	}

	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}
	
	@Override
	protected Object buildDataset(List<MemoryBoot> objects, Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String seriesTitleUptime = context.getString(
				R.string.series_title_uptime);
		String seriesTitleAvgUptime = context.getString(
				R.string.series_title_uptime_avg);
		

		TimeSeries series = new TimeSeries(seriesTitleUptime);

		long time = 0;
		long uptime = 0;
		for (MemoryBoot boot: objects) {
			time = boot.getTime();
			uptime = boot.getBootUpTime() / 1000;
			Logger.debug("[%s] uptime = %d",
					CalendarUtils.timeToReadableString(time),
					uptime);
			
			if (uptime <= 0) {
				continue;
			}
			
			if (sharedArguments instanceof SharedArguments) {
				SharedArguments sArgs = (SharedArguments)sharedArguments;
				
				sArgs.groupInDay(time, uptime);
				sArgs.updateMinAndMaxTime(time);
				sArgs.updateMinAndMaxUptime(uptime);
				sArgs.addSumUptime(uptime);
			}
			
			series.add(new Date(time), uptime);
		}
		
		TimeSeries avgSeries = new TimeSeries(seriesTitleAvgUptime);
		TimeSeries dayAvgSeries = new TimeSeries(seriesTitleUptime);
		if (args != null) {
			args.minTime = getPeroidStart();
			args.maxTime = Math.min(getPeroidEnd(), args.maxTime);
			
			if (args.maxTime < args.minTime + 14 * CalendarUtils.DAY_IN_MILLIS) {
				args.maxTime = args.minTime + 14 * CalendarUtils.DAY_IN_MILLIS;
			}
			
			Logger.debug("args = %s", args);
			
			final long avgUptime = (args.cntUptime == 0 ?
					0 : (args.sumUptime / args.cntUptime));
			
			final long stime = args.minTime - 0 * CalendarUtils.DAY_IN_MILLIS;
			final long etime = args.maxTime + 0 * CalendarUtils.DAY_IN_MILLIS;

			for (time = stime; time <= etime; time += CalendarUtils.DAY_IN_MILLIS) {
				uptime = args.minUptime;
/*				Logger.debug("[%s] uptime = %d",
						CalendarUtils.timeToReadableString(time),
						uptime);
*/
				avgSeries.add(new Date(time), avgUptime);
			}
			
			if (args.dayStarts != null && args.dayStarts.size() > 0) {
				Long cntUptime;
				Long sumUptime;
				
				for (Long dayStart: args.dayStarts) {
					sumUptime = args.daySumUptime.get(dayStart);
					cntUptime = args.dayCntUptime.get(dayStart);
					if (sumUptime != null && sumUptime.longValue() > 0 &&
							cntUptime != null && cntUptime.longValue() > 0) {
						dayAvgSeries.add(new Date(dayStart),
								(sumUptime.longValue() / cntUptime.longValue()));
					}
				}
			}
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (dataset != null) {
			dataset.addSeries(avgSeries);
//			dataset.addSeries(series);
			dataset.addSeries(dayAvgSeries);
		}
		
		return dataset;
	}

	@Override
	protected Object buildRenderer(List<MemoryBoot> objects, Object sharedArguments) {
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
		
		XYSeriesRenderer r1 = new XYSeriesRenderer();
		
		r1.setColor(ChartUtils.getColorResource(context, R.color.light_gray));
		r1.setPointStyle(PointStyle.POINT);
		r1.setFillPoints(true);
		r1.setLineWidth(2);
		r1.setStroke(BasicStroke.DASHED);
		
		XYSeriesRenderer r2 = new XYSeriesRenderer();
		
		r2.setColor(ChartUtils.getColorResource(context, R.color.royal_blue));
		r2.setPointStyle(PointStyle.TRIANGLE);
		r2.setFillPoints(true);
		r2.setLineWidth(1);
		r2.setStroke(BasicStroke.DASHED);
		
		XYSeriesRenderer r3 = new XYSeriesRenderer();
		
		r3.setColor(ChartUtils.getColorResource(context, R.color.royal_blue));
		r3.setPointStyle(PointStyle.CIRCLE);
		r3.setFillPoints(true);
		r3.setLineWidth(3);
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.addSeriesRenderer(r1);
//		renderer.addSeriesRenderer(r2);
		renderer.addSeriesRenderer(r3);
		
		ChartUtils.applyDefaulChartStyle(getContext(), renderer);
		
		final Resources res = context.getResources();
		if (res != null) {
			int chartMarginLeft = 20;
			int chartMarginTop = 20;
			int chartMarginRight = 20;
			int chartMarginBottom = 20;

			chartMarginLeft = res.getDimensionPixelSize(R.dimen.default_chart_margin_left);
			chartMarginTop = res.getDimensionPixelSize(R.dimen.boot_chart_margin_top);
			chartMarginRight = res.getDimensionPixelSize(R.dimen.default_chart_margin_right);
			chartMarginBottom = res.getDimensionPixelSize(R.dimen.default_chart_margin_bottom);
		    
			renderer.setMargins(new int[] { chartMarginTop, chartMarginLeft,
		    		chartMarginBottom, chartMarginRight });
		}

		long xMin = 0; 
		long xMax = 0;
		long yMin = 0;
		long yMax = 0;

		xMin = getPeroidStart();
		xMax = getPeroidEnd();
		
		if (args != null) {
			xMin = args.minTime;
			xMax = args.maxTime;
			yMin = args.minUptime;
			yMax = args.maxUptime;
		}
		
		
    	SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");
    	
    	renderer.setChartTitle(format.format(getPeroidStart()));
    	
		renderer.setXTitle(context.getString(R.string.chart_lable_date));
		renderer.setYTitle(context.getString(R.string.chart_label_seconds));
		renderer.setYAxisMin(yMin / 3);
		renderer.setYAxisMax(yMax + 1);
		
		long xAxisMin = xMin;
		long xAxisMax = xMin + 14 * CalendarUtils.DAY_IN_MILLIS;
		
		final long now = System.currentTimeMillis();
		if (!CalendarUtils.isInRange(now, xMin, xMax)) {
			xAxisMax = xMax;
		}
		
		Logger.debug("XRange[%s - %s]",
				CalendarUtils.timeToReadableString(xMin),
				CalendarUtils.timeToReadableString(xMax));
		Logger.debug("XAxis[%s - %s]",
				CalendarUtils.timeToReadableString(xAxisMin),
				CalendarUtils.timeToReadableString(xAxisMax));
		
		Logger.debug("YAxis[%d - %d]",
				yMin, yMax);
		
		renderer.setXAxisMin(xAxisMin);
		renderer.setXAxisMax(xAxisMax);
		
	    renderer.setXLabels(7);
	    renderer.setYLabels(12);

//	    renderer.setPanEnabled(true);
	    
	    double panRange[] = {
	    	xMin, 
	    	xMax, 
	    	yMin, 
	    	yMax	
	    };
	    
	    double zoomRange[] = {
		    xMin,
			xMax, 
			yMin, 
			yMax	
	    };
		    
	    renderer.setPanLimits(panRange);
	    renderer.setZoomLimits(zoomRange);

		return renderer;
	}

}
