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
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UselessApp;
import com.dailystudio.memory.application.loader.UselessAppsLoader;
import com.dailystudio.memory.application.ui.UselessAppAdapter;
import com.dailystudio.memory.application.ui.UselessAppComparator;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class UselessAppsListFragment extends AbsResObjectListFragment<UselessApp> {

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
	public void onDestroy() {
		super.onDestroy();
		
		NativeObservable appObservable = 
			ObservableManager.getObservable(AppObservable.class);
		if (appObservable != null) {
			appObservable.deleteObserver(mAppObserver);
		}
	}

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
	public Loader<List<UselessApp>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new UselessAppsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new UselessAppAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_USELESS_APP_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}
	
	@Override
	protected Comparator<UselessApp> getComparator() {
        return sUselessAppComparator;
	}
    
    private Comparator<UselessApp> sUselessAppComparator =
            new UselessAppComparator();
		
	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			if (data instanceof Intent == false) {
				return;
			}
			
			Intent i = (Intent)data;
			final String action = i.getAction();
			if (action == null) {
				return;
			}

			final boolean replacing = i.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			
			if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				if (!replacing) {
					restartLoader();
				}
			}
		}
		
	};

}
