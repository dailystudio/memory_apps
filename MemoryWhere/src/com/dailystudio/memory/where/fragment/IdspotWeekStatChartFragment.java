package com.dailystudio.memory.where.fragment;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.loader.IdspotWeekStatChartLoader;

public class IdspotWeekStatChartFragment 
	extends MemoryPeroidChartFragment<IdspotHistory> {

	private ViewGroup mChartStub;
	private GraphicalView mChartView;

	private int mIdspotId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_chart, null);
		
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
	public Loader<List<IdspotHistory>> onCreateLoader(int arg0, Bundle args) {
		int idspotId = -1;
		
		if (args != null) {
			idspotId = args.getInt(Constants.EXTRA_IDSPOT_ID, -1);
		}
		
		return new IdspotWeekStatChartLoader(getActivity(), 
				getPeroidStart(), getPeroidEnd(), idspotId);
	}

	@Override
	protected View getChart(Object dataset, Object renderer) {
		Logger.debug("dataset = %s, renderer = %s", 
				dataset, renderer);
		if (dataset instanceof XYMultipleSeriesDataset == false) {
			return null;
		}
		
		if (renderer instanceof XYMultipleSeriesRenderer == false) {
			return null;
		}
		
		mChartView = ChartFactory.getLineChartView(
				getActivity(), 
				(XYMultipleSeriesDataset)dataset, 
				(XYMultipleSeriesRenderer)renderer);
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
		return LoaderIds.MEMORY_IDSPOT_WEEK_STAT_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_IDSPOT_ID, mIdspotId);
		
		return args;
	}

	public void attachToIdspot(int idspotId) {
		mIdspotId = idspotId;
		
		restartLoader();
	}

}
