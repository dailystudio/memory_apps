package com.dailystudio.memory.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.PluginActivityLoader;
import com.dailystudio.memory.ui.PluginActivityAdapter;


public class PluginActivityListFragment extends AbsMemoryListFragment {
	
	private String mActivitiesCategory = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_activity_list, null);
		
		return view;
	}

	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}

		mActivitiesCategory = intent.getStringExtra(Constants.EXTRA_ACTIVITY_CATEGORY);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		String category = null;
		
		if (args != null) {
			category = args.getString(Constants.EXTRA_ACTIVITY_CATEGORY);
		}
		
		return new PluginActivityLoader(getActivity(), category);
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new PluginActivityAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_PLUGIN_ACTIVITY_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		if (mActivitiesCategory != null) {
			args.putString(Constants.EXTRA_ACTIVITY_CATEGORY, mActivitiesCategory);
		}
		
		return args;
	}

}
