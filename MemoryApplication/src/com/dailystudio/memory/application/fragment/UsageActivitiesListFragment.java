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
import com.dailystudio.memory.application.loader.UsageActivitiesListLoader;
import com.dailystudio.memory.application.ui.UsageActivitiesAdapter;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;
import com.dailystudio.nativelib.application.AndroidActivity;

public class UsageActivitiesListFragment extends AbsResObjectListFragment<AndroidActivity> {
	
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
	public Loader<List<AndroidActivity>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new UsageActivitiesListLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new UsageActivitiesAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_ACTIVITY_USAGES_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	protected Comparator<AndroidActivity> getComparator() {
		return null;
	}

}
