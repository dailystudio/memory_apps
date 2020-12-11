package com.dailystudio.memory.application.fragment;

import java.util.Comparator;
import java.util.List;

import android.content.Intent;
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
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.loader.ActivityUsagesListLoader;
import com.dailystudio.memory.application.ui.UsageAdapter;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;

public class ActivityUsagesListFragment extends AbsResObjectListFragment<Usage> {
	
	public String mPackageName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_act_usage_list, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
	}

	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mPackageName = intent.getStringExtra(Constants.EXTRA_APP_PACKAGE);
	}

	@Override
	public Loader<List<Usage>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		final String pkg = args.getString(Constants.EXTRA_APP_PACKAGE);
		
		return new ActivityUsagesListLoader(getActivity(), 
				getPeroidStart(), 
				getPeroidEnd(), 
				pkg);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new UsageAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_ACTIVITY_USAGES_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle arg = new Bundle();
		
		arg.putString(Constants.EXTRA_APP_PACKAGE, mPackageName);
		
		return arg;
	}
	
	public void loadUdageForPackage(String pkg) {
		if (pkg == null) {
			return;
		}
		
		mPackageName = pkg;
		
		restartLoader();
	}
	
	@Override
	protected Comparator<Usage> getComparator() {
		return null;
	}

}
