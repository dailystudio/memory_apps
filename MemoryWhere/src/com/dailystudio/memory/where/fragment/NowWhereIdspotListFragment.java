package com.dailystudio.memory.where.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.memory.fragment.MemoryPeroidObjectsListFragment;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.loader.NowWhereIdspotListLoader;
import com.dailystudio.memory.where.ui.NowWhereIdspotAdapter;

public class NowWhereIdspotListFragment 
    extends MemoryPeroidObjectsListFragment<IdentifiedHotspot> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_now_where_idspot_list, null);
		
		return view;
	}

	@Override
	public Loader<List<IdentifiedHotspot>> onCreateLoader(int loaderId, Bundle args) {
		return new NowWhereIdspotListLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new NowWhereIdspotAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_NW_IDENTIFIED_HOTSPOT_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
}
