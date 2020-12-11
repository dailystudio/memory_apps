package com.dailystudio.memory.mood.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.dailystudio.memory.fragment.MemoryPeroidCursorListFragment;
import com.dailystudio.memory.mood.LoaderIds;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.mood.loader.MoodDayListLoader;
import com.dailystudio.memory.mood.ui.MemoryMoodAdapter;

public class MoodByDayListFragment extends MemoryPeroidCursorListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new MoodDayListLoader(getActivity(), getPeroidStart());
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new MemoryMoodAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_MOOD_BY_DAY_LIST_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		super.onItemClick(l, v, position, id);
		
	}

}
