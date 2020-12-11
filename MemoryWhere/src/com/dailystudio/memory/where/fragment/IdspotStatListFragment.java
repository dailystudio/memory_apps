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
import com.dailystudio.memory.where.databaseobject.IdspotHistorySummary;
import com.dailystudio.memory.where.loader.IdspotStatListLoader;
import com.dailystudio.memory.where.ui.IdentifiedHotspotAdapter;

public class IdspotStatListFragment 
    extends MemoryPeroidObjectsListFragment<IdspotHistorySummary> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<List<IdspotHistorySummary>> onCreateLoader(int loaderId, Bundle args) {
		return new IdspotStatListLoader(getActivity(), getPeroidStart(), getPeroidEnd());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new IdentifiedHotspotAdapter(getActivity(),
				 getPeroidStart(), getPeroidEnd());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_IDENTIFIED_HOTSPOT_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
