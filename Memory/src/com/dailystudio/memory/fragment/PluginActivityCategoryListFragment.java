package com.dailystudio.memory.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.PluginCategoryLoader;
import com.dailystudio.memory.ui.PluginActivityCategoryAdapter;


public class PluginActivityCategoryListFragment extends AbsMemoryListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_category_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new PluginCategoryLoader(getActivity());
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new PluginActivityCategoryAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_PLUGIN_CATEGORY_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
