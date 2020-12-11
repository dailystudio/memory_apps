package com.dailystudio.memory.application.fragment;

import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.application.loader.AppInstHistoriesLoader;
import com.dailystudio.memory.application.ui.AppInstHistoryAdapter;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;

public class AppInstHistoriesListFragment extends AbsResObjectListFragment<AppInstHistory> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
	}

	@Override
	public Loader<List<AppInstHistory>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new AppInstHistoriesLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new AppInstHistoryAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_APP_INST_HISTORY_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	protected Comparator<AppInstHistory> getComparator() {
		return null;
	}

}
