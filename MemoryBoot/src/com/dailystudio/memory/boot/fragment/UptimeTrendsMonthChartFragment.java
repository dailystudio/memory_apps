package com.dailystudio.memory.boot.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.UptimeTrendsMonthChartLoader;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;

public class UptimeTrendsMonthChartFragment 
	extends MemoryPeroidChartFragment<MemoryBoot> {

	private ViewGroup mChartStub;

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
	public Loader<List<MemoryBoot>> onCreateLoader(int arg0, Bundle arg1) {
		return new UptimeTrendsMonthChartLoader(getActivity(), 
				getPeroidStart(), getPeroidEnd());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_BOOT_UPTIME_TRENDS_MONTH_CHART_LOADER;
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
	    		"dd");
	}

	@Override
	protected ViewGroup getChartHolder() {
		return mChartStub;
	}

}
