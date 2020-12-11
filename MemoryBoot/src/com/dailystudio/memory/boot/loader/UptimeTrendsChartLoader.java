package com.dailystudio.memory.boot.loader;

import java.util.Date;
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
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryUptimeAverage;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.loader.ProjectedDatabaseChartLoader;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class UptimeTrendsChartLoader extends ProjectedDatabaseChartLoader<MemoryBoot, MemoryUptimeAverage> {

	private class SharedArguments {
		
		private long minTime = 0;
		private long maxTime = 0;
		
		private long minUptime = 0;
		private long maxUptime = 0;
		
		private long sumUptime = 0;
		private long cntUptime = 0;
		
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
		
		@Override
		public String toString() {
			return String.format("time[%s - %s], uptime[%d - %d], stat[sum = %d, cnt = %d, avg = %d]",
					CalendarUtils.timeToReadableString(minTime),
					CalendarUtils.timeToReadableString(maxTime),
					minUptime, maxUptime,
					sumUptime, cntUptime, 
					(cntUptime == 0 ? 0 : (sumUptime / cntUptime)));
		}

	}
	
	public UptimeTrendsChartLoader(Context context) {
		super(context);
	}

	@Override
	protected Query getQuery(Class<MemoryBoot> klass) {
		Query query = new Query(MemoryBoot.class);
		
		ExpressionToken selToken = 
			MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken orderBy = MemoryBoot.COLUMN_TIME.orderByDescending();
		if (orderBy != null) {
			query.setOrderBy(orderBy);
		}

		OrderingToken groupBy = MemoryBoot.COLUMN_TIME.groupByWeek();
		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
		
		return query;
	}
	
	@Override
	protected Class<MemoryBoot> getObjectClass() {
		return MemoryBoot.class;
	}

	@Override
	protected Class<MemoryUptimeAverage> getProjectionClass() {
		return MemoryUptimeAverage.class;
	}

	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}
	
	@Override
	protected Object buildDataset(List<MemoryUptimeAverage> objects, Object sharedArguments) {
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
		MemoryUptimeAverage bootAvg = null;
		for (DatabaseObject object: objects) {
			if (object instanceof MemoryUptimeAverage == false) {
				continue;
			}
			
			bootAvg = ((MemoryUptimeAverage)object);
			
			time = bootAvg.getWeekTime();
			uptime = (long) Math.round(bootAvg.getUptimeAverage() / 1000);
			Logger.debug("[%s] uptime = %d",
					CalendarUtils.timeToReadableString(time),
					uptime);
			
			if (uptime <= 0) {
				continue;
			}
			
			if (sharedArguments instanceof SharedArguments) {
				SharedArguments sArgs = (SharedArguments)sharedArguments;
				
				sArgs.updateMinAndMaxTime(time);
				sArgs.updateMinAndMaxUptime(uptime);
				sArgs.addSumUptime(uptime);
			}
			
			series.add(new Date(time), uptime);
		}
		
		TimeSeries avgSeries = new TimeSeries(seriesTitleAvgUptime);
		if (args != null) {
			Logger.debug("args = %s", args);
			
			final long avgUptime = (args.cntUptime == 0 ?
					0 : (args.sumUptime / args.cntUptime));
			
			final long stime = args.minTime - 2 * CalendarUtils.DAY_IN_MILLIS;
			final long etime = args.maxTime + 2 * CalendarUtils.DAY_IN_MILLIS;

			for (time = stime; time <= etime; time += CalendarUtils.DAY_IN_MILLIS) {
				uptime = args.minUptime;
/*				Logger.debug("[%s] uptime = %d",
						CalendarUtils.timeToReadableString(time),
						uptime);
*/
				avgSeries.add(new Date(time), avgUptime);
			}
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (dataset != null) {
			dataset.addSeries(avgSeries);
			dataset.addSeries(series);
		}
		
		return dataset;
	}

	@Override
	protected Object buildRenderer(List<MemoryUptimeAverage> objects, Object sharedArguments) {
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
		
		renderer.setChartTitle(String.format("%s - %s",
				DateTimePrintUtils.printTimeStringWithoutTime(context, args.minTime),
				DateTimePrintUtils.printTimeStringWithoutTime(context, args.maxTime)));
    	
		renderer.setXTitle(context.getString(R.string.chart_lable_date));
		renderer.setYTitle(context.getString(R.string.chart_label_seconds));
		renderer.setYAxisMin(yMin / 2);
		renderer.setYAxisMax(yMax + 1);
		
		long xAxisMin = xMin - 2 * CalendarUtils.DAY_IN_MILLIS;
		long xAxisMax = xMax + 2 * CalendarUtils.DAY_IN_MILLIS;
		
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

}
