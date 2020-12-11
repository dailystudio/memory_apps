package com.dailystudio.memory.where.chart;

import java.util.List;

import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartUtils;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.IdentifiedHotspotColors;

public class MyPlacesPieChartBuilder extends AbsIdspotStatPieChartBuilder {

	public MyPlacesPieChartBuilder(Context context, long start, long end) {
		super(context, start, end);
	}

	@Override
	public Object buildRenderer(List<IdspotHistory> objects,
			Object sharedArguments) {
		Logger.debug("sharedArguments = %s",
				sharedArguments);
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
		
		if (args.mSumDurations == null ||
				args.mSumDurations.size() <= 0) {
			return null;
		}
		
	    DefaultRenderer renderer = new DefaultRenderer();
	    
	    ChartUtils.applyDefaulChartStyle(context, renderer);
	    
		List<HotspotIdentity> sortedIdentities = 
				sortIdentities(args.mSumDurations.keySet());
	    if (sortedIdentities == null) {
	    	return null;
	    }
		
	    int colorResId = 0;
		for (HotspotIdentity identity: sortedIdentities) {
			Logger.debug("identity: %s, colorResId = %d, loader = 0x%08x", 
					identity,
					colorResId, 
					this.hashCode());
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			
			colorResId = IdentifiedHotspotColors.getColorOfHotspotIdentity(
					identity);
			
			r.setColor(ChartUtils.getColorResource(context, colorResId));
			r.setDisplayChartValues(true);
			r.setDisplayChartValues(true);
			
			renderer.addSeriesRenderer(r);
	    }
		
		renderer.setShowLabels(false);
		renderer.setZoomEnabled(false);
		renderer.setPanEnabled(false);
		
		renderer.setShowLegend(false);
	    renderer.setMargins(new int[] { 0, 0, 0, 0 });
	    renderer.setLabelsTextSize(0);
	    renderer.setChartTitleTextSize(0);
		renderer.setLegendTextSize(0);
		renderer.setLegendHeight(0);
		renderer.setFitLegend(false);

		return renderer;
	}
	
}
