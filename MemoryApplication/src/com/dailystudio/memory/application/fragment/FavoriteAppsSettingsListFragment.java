package com.dailystudio.memory.application.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.app.fragment.AbsArrayAdapterFragment;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.FavoriteAppsSetting;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.loader.FavoriteAppsSettingsLoader;
import com.dailystudio.memory.application.ui.FavoriteAppsSettingsAdapter;

public class FavoriteAppsSettingsListFragment extends AbsArrayAdapterFragment<FavoriteAppsSetting> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_favorite_apps_settings_list, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
	}

	@Override
	public Loader<List<FavoriteAppsSetting>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new FavoriteAppsSettingsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new FavoriteAppsSettingsAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_FAVORITE_APPS;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
