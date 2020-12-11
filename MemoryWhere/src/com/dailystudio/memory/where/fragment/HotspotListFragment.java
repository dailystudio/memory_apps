package com.dailystudio.memory.where.fragment;

import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.memory.fragment.MemoryPeroidObjectsListFragment;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.loader.HotspotListLoader;
import com.dailystudio.memory.where.ui.HotspotAdapter;

public class HotspotListFragment extends MemoryPeroidObjectsListFragment<Hotspot> {

	private long mHotspotNum;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<List<Hotspot>> onCreateLoader(int loaderId, Bundle args) {
		final long end = System.currentTimeMillis();
		final long start = end - Constants.HOTSPOT_PRE_CANDIDATE_TIME_SPAN;
		
		return new HotspotListLoader(getActivity(), 
				start, end, mHotspotNum);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new HotspotAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_HOTSPOT_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	@Override
	protected Comparator<Hotspot> getComparator() {
	    return sComparator;
	}
	
	private static Comparator<Hotspot> sComparator = 
	        new Hotspot.HotspotComparator();
	
}
