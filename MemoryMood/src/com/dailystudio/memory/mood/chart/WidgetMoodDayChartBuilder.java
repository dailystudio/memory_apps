package com.dailystudio.memory.mood.chart;

import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.Moods;
import com.dailystudio.memory.mood.R;

public class WidgetMoodDayChartBuilder extends BaseMoodDayChartBuilder {

	public WidgetMoodDayChartBuilder(Context context, long timeOfDay) {
		super(context, timeOfDay);
	}

	@Override
	public Object buildRenderer(List<MemoryMood> objects,
			Object sharedArguments) {
//		Logger.debug("objects = [%s]", objects);

		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		XYSeriesRenderer r1 = new XYSeriesRenderer();
		
		r1.setColor(ChartUtils.getColorResource(context, R.color.light_gray));
		r1.setPointStyle(PointStyle.CIRCLE);
		r1.setFillPoints(true);
		r1.setLineWidth(2);
		r1.setStroke(BasicStroke.DASHED);

		XYSeriesRenderer r2 = new XYSeriesRenderer();
		
		r2.setColor(ChartUtils.getColorResource(context, R.color.tomato_red));
		r2.setPointStyle(PointStyle.CIRCLE);
		r2.setFillPoints(true);
		r2.setLineWidth(3);
		
		long xMin = 0;
		long xMax = 0;
		long yMin = Moods.getMinMoodLevel() - 1;
		long yMax = Moods.getMaxMoodLevel() + 1;
		
		final long timeOfDay = getTimeOfDay();
		
		xMin = CalendarUtils.getStartOfDay(timeOfDay);
		xMax = CalendarUtils.getEndOfDay(timeOfDay);
		
		Logger.debug("XAxis[%s - %s]",
				CalendarUtils.timeToReadableString(xMin),
				CalendarUtils.timeToReadableString(xMax));
		
		Logger.debug("YAxis[%d - %d]",
				yMin, yMax);
		
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		renderer.addSeriesRenderer(r1);
		if (objects != null && objects.size() > 0) {
			renderer.addSeriesRenderer(r2);
		}
		
//    	renderer.setChartTitle(DateTimePrintUtils.printTimeStringWithoutTime(
// 				context, timeOfDay));
    	
//		renderer.setXTitle(context.getString(R.string.chart_label_hours));
//		renderer.setYTitle(context.getString(R.string.chart_label_levels));
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		
		long xAxisMin = (timeOfDay - 24 * CalendarUtils.HOUR_IN_MILLIS);
		if (xAxisMin < xMin) {
			xAxisMin = xMin;
		}
		
		renderer.setXAxisMin(xAxisMin);
		renderer.setXAxisMax(xMax);
		renderer.setAntialiasing(true);
		
	    renderer.setXLabels(12);
	    renderer.setYLabels(6);

	    ChartUtils.applyDefaulWidgetChartStyle(getContext(), renderer);

		return renderer;
	}

}
