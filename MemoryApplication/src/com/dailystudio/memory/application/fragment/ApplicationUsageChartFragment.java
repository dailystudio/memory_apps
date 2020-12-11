package com.dailystudio.memory.application.fragment;

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
import android.widget.TextView;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.application.loader.ApplicationUsageChartLoader;
import com.dailystudio.memory.application.loader.ApplicationUsageChartLoader.StatisticsResult;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class ApplicationUsageChartFragment 
	extends MemoryPeroidChartFragment<UsageStatistics> {

	public String mPackageName;
	
	private ViewGroup mChartStub;
	private ViewGroup mExtraViews;
	
	private TextView mDayUseFreq;
	private TextView mDayUseAvg;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_usage_chart, null);
		
		setupViews(view);
		
		return view;
	}
	
	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mPackageName = intent.getStringExtra(Constants.EXTRA_APP_PACKAGE);
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mChartStub = (ViewGroup) fragmentView.findViewById(
				R.id.chartStub);
		
		mExtraViews = (ViewGroup) fragmentView.findViewById(
				R.id.extra_views);
		
		mDayUseFreq = (TextView) fragmentView.findViewById(
				R.id.app_usage_day_use_freq);
		mDayUseAvg = (TextView) fragmentView.findViewById(
				R.id.app_usage_day_use_avg);
	}

	@Override
	protected ViewGroup getChartHolder() {
		return mChartStub;
	}

	@Override
	public Loader<List<UsageStatistics>> onCreateLoader(int loaderId, Bundle args) {
		if (args == null) {
			return null;
		}
		
		final String pkg = args.getString(Constants.EXTRA_APP_PACKAGE);

		return new ApplicationUsageChartLoader(getActivity(),
				getPeroidStart(),
				getPeroidEnd(),
				pkg);
	}

	@Override
	protected View getChart(Object dataset, Object renderer) {
		if (dataset instanceof XYMultipleSeriesDataset == false) {
			return null;
		}
		
		if (renderer instanceof XYMultipleSeriesRenderer == false) {
			return null;
		}
		
		final boolean dailyChart = 
				((getPeroidEnd() - getPeroidStart()) < CalendarUtils.DAY_IN_MILLIS);
				Logger.debug("dailyChart = %s", dailyChart);
				
		View chart = null;
		if (dailyChart) {
			chart = ChartFactory.getBarChartView(
				getActivity(), 
	    		(XYMultipleSeriesDataset)dataset, 
	    		(XYMultipleSeriesRenderer)renderer,
	    		Type.STACKED);
		} else {
			chart = ChartFactory.getTimeChartView(
	    		getActivity(), 
	    		(XYMultipleSeriesDataset)dataset, 
	    		(XYMultipleSeriesRenderer)renderer, 
	    		"MM/dd");
		}
		
		if (mExtraViews != null) {
			mExtraViews.setVisibility(dailyChart ?
					View.GONE : View.VISIBLE);
		}
		
		return chart;
	}
	
	@Override
	public void onLoadFinished(Loader<List<UsageStatistics>> loader,
			List<UsageStatistics> data) {
		super.onLoadFinished(loader, data);
		
		if (loader instanceof ApplicationUsageChartLoader == false) {
			return;
		}
		
		ApplicationUsageChartLoader aucloader = 
				(ApplicationUsageChartLoader)loader;
		
		updateStatistics(aucloader.getResult());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_APPLICATION_USAGE_CHART;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle arg = new Bundle();
		
		arg.putString(Constants.EXTRA_APP_PACKAGE, mPackageName);
		
		return arg;
	}

	public void loadUdageForPackage(String pkg) {
		if (pkg == null) {
			return;
		}
		
		mPackageName = pkg;
		
		restartLoader();
	}
	
	private void updateStatistics(StatisticsResult result) {
		if (result == null) {
			return;
		}
		
		if (mDayUseFreq != null) {
			int dayUseFreq = (int)result.getDayUseFreq();
			
			if (dayUseFreq < 0) {
				dayUseFreq = 0;
			}
			
			String text = getResources().getQuantityString(
					R.plurals.app_usage_day_use_freq_templ, 
					dayUseFreq,
					dayUseFreq);
			Logger.debug("dayUseFreq = %d text = %s",  
					dayUseFreq, text);
			
			mDayUseFreq.setText(text);
		}
		
		if (mDayUseAvg != null) {
			final long dayUseAvg = result.getDayUseAverage();
			
			mDayUseAvg.setText(
					DateTimePrintUtils.printDurationString(getActivity(), 
							dayUseAvg));
		}
		
	}


}
