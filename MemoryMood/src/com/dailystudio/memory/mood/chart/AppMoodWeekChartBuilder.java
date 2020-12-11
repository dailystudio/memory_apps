package com.dailystudio.memory.mood.chart;

import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.mood.MemoryMoodWeekdayAverage;
import com.dailystudio.memory.mood.Moods;
import com.dailystudio.memory.mood.R;

import android.content.Context;
import android.content.res.Resources;

public class AppMoodWeekChartBuilder extends BaseMoodWeekChartBuilder {

	public AppMoodWeekChartBuilder(Context context) {
		super(context, -1, -1);
	}

	public AppMoodWeekChartBuilder(Context context, long start, long end) {
		super(context, start, end);
	}

	@Override
	public Object buildRenderer(List<MemoryMoodWeekdayAverage> objects, Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		XYSeriesRenderer r1 = new XYSeriesRenderer();
		
		r1.setColor( ChartUtils.getColorResource(context, R.color.light_gray));
		r1.setPointStyle(PointStyle.CIRCLE);
		r1.setFillPoints(true);
		r1.setLineWidth(2);
		r1.setStroke(BasicStroke.DASHED);

		XYSeriesRenderer r2 = new XYSeriesRenderer();
		
		r2.setColor( ChartUtils.getColorResource(context, R.color.tomato_red));
		r2.setPointStyle(PointStyle.CIRCLE);
		r2.setFillPoints(true);
		r2.setLineWidth(3);
		
		long yMin = Moods.getMinMoodLevel() - 1;
		long yMax = Moods.getMaxMoodLevel() + 1;

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		renderer.addSeriesRenderer(r1);
		renderer.addSeriesRenderer(r2);
		
		 ChartUtils.applyDefaulChartStyle(getContext(), renderer);
		
		final Resources res = context.getResources();
		if (res != null) {
			int chartMarginLeft = 20;
			int chartMarginTop = 20;
			int chartMarginRight = 20;
			int chartMarginBottom = 20;

			chartMarginLeft = res.getDimensionPixelSize(R.dimen.default_chart_margin_left);
			chartMarginTop = res.getDimensionPixelSize(R.dimen.mood_chart_margin_top);
			chartMarginRight = res.getDimensionPixelSize(R.dimen.default_chart_margin_right);
			chartMarginBottom = res.getDimensionPixelSize(R.dimen.default_chart_margin_bottom);
		    
			renderer.setMargins(new int[] { chartMarginTop, chartMarginLeft,
		    		chartMarginBottom, chartMarginRight });
		}
		
		long weekStart = getPeroidStart();
		long weekEnd = getPeroidEnd();
		
		if (weekStart <= 0 || weekEnd <= 0 || weekEnd <= weekStart) {
			final long now = System.currentTimeMillis();
			weekStart = CalendarUtils.getStartOfWeek(now);
			weekEnd = CalendarUtils.getEndOfWeek(now);
		}

		renderer.setChartTitle(String.format("%d/%d - %d/%d",
				CalendarUtils.getMonth(weekStart) + 1,
				CalendarUtils.getDay(weekStart),
				CalendarUtils.getMonth(weekEnd) + 1,
				CalendarUtils.getDay(weekEnd)));

		renderer.setXTitle(context.getString(R.string.chart_label_weekdays));
		renderer.setYTitle(context.getString(R.string.chart_label_levels));

		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(7.5);
		
	    renderer.setXLabels(7);
	    renderer.setYLabels((int)(yMax - yMin));

	    renderer.setPanEnabled(false, false);
	    renderer.setPanLimits(new double[] { 
	    		0, 7, 
	    		yMin,
	    		yMax});

		return renderer;
	}

}
