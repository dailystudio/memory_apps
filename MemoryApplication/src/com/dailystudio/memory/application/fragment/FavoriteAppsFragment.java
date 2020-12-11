package com.dailystudio.memory.application.fragment;

import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.loader.FavoriteAppsLoader;
import com.dailystudio.memory.application.ui.FavoriteAppsAdapter;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class FavoriteAppsFragment extends AbsResObjectListFragment<FavoriteApp> {

	private boolean mIncludeSysApps = false;
	private int mFavoriteClass = Constants.FAVORITE_CLASS_WEEK;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		NativeObservable appObservable = 
			ObservableManager.getObservable(AppObservable.class);
		if (appObservable != null) {
			appObservable.addObserver(mAppObserver);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_favorite_apps, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		NativeObservable appObservable = 
			ObservableManager.getObservable(AppObservable.class);
		if (appObservable != null) {
			appObservable.deleteObserver(mAppObserver);
		}
	}

	@Override
	public void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mFavoriteClass = intent.getIntExtra(Constants.EXTRA_FAVORITE_CLASS,
				Constants.FAVORITE_CLASS_WEEK);
		mIncludeSysApps = intent.getBooleanExtra(
				Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS,
				false);
	}
	
	@Override
	public Loader<List<FavoriteApp>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new FavoriteAppsLoader(getActivity(),
				mIncludeSysApps, mFavoriteClass);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new FavoriteAppsAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_FAVORITE_APPS;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	protected Comparator<FavoriteApp> getComparator() {
	    return null;
	}

	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};

}
