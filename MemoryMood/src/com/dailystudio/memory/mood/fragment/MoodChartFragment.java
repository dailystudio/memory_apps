package com.dailystudio.memory.mood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.memory.fragment.MemoryPeroidChartFragment;
import com.dailystudio.memory.mood.R;

public abstract class MoodChartFragment<P extends DatabaseObject>
	extends MemoryPeroidChartFragment<P> {

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
	protected ViewGroup getChartHolder() {
		return mChartStub;
	}

}
