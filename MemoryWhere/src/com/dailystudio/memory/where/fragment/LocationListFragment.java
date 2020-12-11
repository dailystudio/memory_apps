package com.dailystudio.memory.where.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dailystudio.memory.fragment.MemoryPeroidCursorListFragment;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.loader.LocationCursorListLoader;
import com.dailystudio.memory.where.ui.MemoryLocationAdapter;

public class LocationListFragment extends MemoryPeroidCursorListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new LocationCursorListLoader(getActivity());
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new MemoryLocationAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_LOC_CURSOR_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		super.onLoadFinished(loader, data);
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		super.onItemClick(l, v, position, id);
	}

}
