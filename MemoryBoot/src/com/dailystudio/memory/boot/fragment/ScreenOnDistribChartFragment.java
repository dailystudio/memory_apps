package com.dailystudio.memory.boot.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.boot.Constants;
import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.MemoryScreenOn;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.ScreenOnDistribChartLoader;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;

public class ScreenOnDistribChartFragment 
	extends MemoryPeroidChartFragment<MemoryScreenOn> {

	private ViewGroup mChartStub;
	
	private int[] mFilterWeekDays;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chart, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mChartStub = (ViewGroup) fragmentView.findViewById(
				R.id.chartStub);
	}

	@Override
	public Loader<List<MemoryScreenOn>> onCreateLoader(int arg0, Bundle arg1) {
		return new ScreenOnDistribChartLoader(getActivity(),
				getPeroidStart(), getPeroidEnd(), mFilterWeekDays);
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_SCREEN_ON_DISTRUB_CHART_LOADER;
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
		
		return ChartFactory.getBarChartView(
				getActivity(), 
	    		(XYMultipleSeriesDataset)dataset, 
	    		(XYMultipleSeriesRenderer)renderer,
	    		Type.STACKED);
	}

	@Override
	protected ViewGroup getChartHolder() {
		return mChartStub;
	}
	
	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mFilterWeekDays = 
				intent.getIntArrayExtra(Constants.EXTRA_FILTER_WEEKDAYS);
	}

}
