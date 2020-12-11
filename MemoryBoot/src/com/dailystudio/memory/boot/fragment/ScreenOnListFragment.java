package com.dailystudio.memory.boot.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dailystudio.memory.boot.Constants;
import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.MemoryScreenOnLoader;
import com.dailystudio.memory.boot.ui.MemoryScreenOnAdapter;
import com.dailystudio.memory.fragment.MemoryPeroidCursorListFragment;

public class ScreenOnListFragment extends MemoryPeroidCursorListFragment {

	private int[] mFilterWeekDays;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new MemoryScreenOnLoader(getActivity(), 
				getPeroidStart(), getPeroidEnd(), mFilterWeekDays);
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new MemoryScreenOnAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_SCREEN_ON_LIST_LOADER;
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
	
	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mFilterWeekDays = 
				intent.getIntArrayExtra(Constants.EXTRA_FILTER_WEEKDAYS);
	}

}
