package com.dailystudio.memory.mood.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.dailystudio.app.fragment.AbsArrayAdapterFragment;
import com.dailystudio.memory.mood.LoaderIds;
import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.mood.loader.MoodsLoader;
import com.dailystudio.memory.mood.ui.MoodsAdatper;

public class WhatIsYourMoodFragment extends AbsArrayAdapterFragment<Mood> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_what_is_your_mood, null);
		
		return view;
	}
	
	@Override
	public Loader<List<Mood>> onCreateLoader(int loaderId, Bundle args) {
		return new MoodsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new MoodsAdatper(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_MOODS_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	public void toogleMoodDisplayMode() {
		ListAdapter adapter = getAdapter();
		if (adapter instanceof MoodsAdatper == false) {
			return;
		}
		
		MoodsAdatper moodsAdapter = (MoodsAdatper)adapter;
		
		if (moodsAdapter.isLevelDisplayed()) {
			moodsAdapter.setLevelDisplayed(false);
		} else {
			moodsAdapter.setLevelDisplayed(true);
		}
	}
	
	public boolean isMoodLevelDisplayed() {
		ListAdapter adapter = getAdapter();
		if (adapter instanceof MoodsAdatper == false) {
			return false;
		}
		
		MoodsAdatper moodsAdapter = (MoodsAdatper)adapter;
		
		return moodsAdapter.isLevelDisplayed();
	}

}
