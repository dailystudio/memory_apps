package com.dailystudio.memory.mood.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;

import com.dailystudio.memory.mood.LoaderIds;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.loader.MoodDayChartLoader;

public class MoodByDayChartFragment extends MoodChartFragment<MemoryMood> {

	@Override
	public Loader<List<MemoryMood>> onCreateLoader(int loaderId, Bundle args) {
		return new MoodDayChartLoader(getActivity(), getPeroidStart());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_MOOD_BY_DAY_CHART_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	@Override
	protected View getChart(Object dataset, Object renderer) {
		if (dataset instanceof XYMultipleSeriesDataset == false) {
			return null;
		}
		
		if (renderer instanceof XYMultipleSeriesRenderer == false) {
			return null;
		}
		
		return  ChartFactory.getTimeChartView(
	    		getActivity(), 
	    		(XYMultipleSeriesDataset)dataset, 
	    		(XYMultipleSeriesRenderer)renderer, 
	    		"HH");
	}

}
