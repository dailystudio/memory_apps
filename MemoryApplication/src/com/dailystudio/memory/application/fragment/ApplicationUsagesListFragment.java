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
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.application.loader.ApplicationUsagesLoader;
import com.dailystudio.memory.application.ui.UsageStatisticsAdapter;
import com.dailystudio.memory.application.ui.UsageStatisticsComparator;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;

public class ApplicationUsagesListFragment extends AbsResObjectListFragment<UsageStatistics> {

	private int mFilterFlags = Constants.USER_APP_ONLY_FILTER_FLAGS;
	
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
	protected int getResolveStartProgress() {
		return 50;
	}
	
	public void setFilterFlags(int filterFlags) {
		mFilterFlags = filterFlags;
		
		restartLoader();
	}

	@Override
	public Loader<List<UsageStatistics>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s, [%s - %s]",
				loaderId,
				args,
				getPeroidStart(),
				getPeroidEnd());
		
		int filterFlags = Constants.ALL_APP_FILTER_FLAGS;
		
		if (args != null) {
			filterFlags = args.getInt(Constants.EXTRA_APP_FILTER_FLAGS, 
					filterFlags);
		}
		
		return new ApplicationUsagesLoader(
				getActivity(), 
				getPeroidStart(),
				getPeroidEnd(),
				filterFlags);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new UsageStatisticsAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_APPLICATION_USAGES_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_APP_FILTER_FLAGS, mFilterFlags);
		
		return args;
	}
	
	@Override
	protected Comparator<UsageStatistics> getComparator() {
        return sUsageStatisticsComparator;
	}

	private Comparator<UsageStatistics> sUsageStatisticsComparator =
		new UsageStatisticsComparator();
	
}
