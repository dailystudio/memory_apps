package com.dailystudio.memory.fragment;

import com.dailystudio.app.dataobject.CountObject;
import com.dailystudio.app.ui.CountView;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.StatisticsPiecesLoader;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatisticsPiecesFragment extends AbsStatisticsFragment<CountObject> {
	
	public static final String STATS_TOKEN = "stats-piece";
	
	private CountView mPiecesValue;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_stats_pieces, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mPiecesValue = (CountView) fragmentView.findViewById(R.id.stats_pieces_value);
		if (mPiecesValue != null) {
			mPiecesValue.setOnCountListener(new CountView.OnCountListener() {
				
				@Override
				public void onCountStart(CountView cv, long destCount) {
				}
				
				@Override
				public void onCountFinished(CountView cv, long destCount) {
					notifyStatisticEnd();
				}
				
				@Override
				public void onCountAbort(CountView cv, long destCount) {
					notifyStatisticEnd();
				}
				
			});
		}
	}
	
	@Override
	public Loader<CountObject> onCreateLoader(int arg0, Bundle arg1) {
		notifyStatisticBegin();
		
		return new StatisticsPiecesLoader(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_STATISTICS_PIECE_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<CountObject> arg0, CountObject data) {
		if (data != null) {
			final int count = data.getCount();
			
			if (mPiecesValue != null) {
				mPiecesValue.countTo(count);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<CountObject> arg0) {
		notifyStatisticEnd();
	}

	@Override
	String getStatisticToken() {
		return STATS_TOKEN;
	}

}
