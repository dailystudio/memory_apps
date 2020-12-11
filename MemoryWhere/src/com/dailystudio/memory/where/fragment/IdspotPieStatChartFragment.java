package com.dailystudio.memory.where.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.loader.IdspotStatPieChartLoader;

public class IdspotPieStatChartFragment 
	extends MemoryPeroidChartFragment<IdspotHistory> {

	private ViewGroup mChartStub;
	private GraphicalView mChartView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_idspot_day_stat_chart, null);
		
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
	protected ViewGroup getChartHolder() {
		return mChartStub;
	}

	@Override
	public Loader<List<IdspotHistory>> onCreateLoader(int arg0, Bundle arg1) {
		return new IdspotStatPieChartLoader(getActivity(), 
				getPeroidStart(),
				getPeroidEnd());
	}

	@Override
	protected View getChart(Object dataset, Object renderer) {
		Logger.debug("dataset = %s, renderer = %s", dataset, renderer);
		if (dataset instanceof CategorySeries == false) {
			return null;
		}
		
		if (renderer instanceof DefaultRenderer == false) {
			return null;
		}
		
		Logger.debug("dataset.size() = %d, renderer.size() = %d", 
				((CategorySeries)dataset).getItemCount(), 
	    		((DefaultRenderer)renderer).getSeriesRenderers().length);
		mChartView = ChartFactory.getPieChartView(
				getActivity(), 
	    		(CategorySeries)dataset, 
	    		(DefaultRenderer)renderer);
		if (mChartView != null) {
			mChartView.setOnClickListener(new View.OnClickListener() {
				
		          @Override
		          public void onClick(View v) {
		            SeriesSelection seriesSelection = 
		            		mChartView.getCurrentSeriesAndPoint();
		            if (seriesSelection == null) {
		            	Logger.debug("No item clicked");
		            } else {
		            	Logger.debug("index = %d, value = %f",
		            			seriesSelection.getPointIndex(),
		            			seriesSelection.getValue());
		            }
		          }
		        });
		}
		
		return mChartView;
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_IDSPOT_DAY_STAT_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
