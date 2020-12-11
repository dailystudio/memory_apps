package com.dailystudio.memory.boot.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.MemoryBootLoader;
import com.dailystudio.memory.boot.ui.MemoryBootAdapter;
import com.dailystudio.memory.fragment.MemoryPeroidCursorListFragment;

public class BootListFragment extends MemoryPeroidCursorListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new MemoryBootLoader(getActivity());
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new MemoryBootAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_BOOT_LIST_LOADER;
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
		
		final ListAdapter adapter = getAdapter();
		if (adapter instanceof MemoryBootAdapter == false) {
			return;
		}
		
		final MemoryBootAdapter mbAdapter = (MemoryBootAdapter)adapter;
		
		if (mbAdapter.isItemExpanded(position) == false) {
			mbAdapter.expandItem(position);
		} else {
			mbAdapter.collapseItem();
		}
	}

}
