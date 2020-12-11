package com.dailystudio.memory.fragment;

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
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.MainPageShortcutsLoader;
import com.dailystudio.memory.ui.MainPageShortcutsAdapter;
import com.dailystudio.nativelib.application.AndroidActivity;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class MainPageShortcutsFragment extends AbsResObjectListFragment<AndroidActivity> {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initObservables();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	
		clearObservables();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mp_shortcuts, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
		setResolveProgressEnabled(false);
	}

	@Override
	public Loader<List<AndroidActivity>> onCreateLoader(int arg0, Bundle arg1) {
		return new MainPageShortcutsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new MainPageShortcutsAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_MAINPAGE_SHORTCUTS_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}
	
	
	private void initObservables() {
		NativeObservable observable = null;
		
		observable = ObservableManager.getObservable(AppObservable.class);
		Logger.debug("observable: %s", observable);
		if (observable != null) {
			observable.addObserver(mAppObserver);
		}
	}
	
	private void clearObservables() {
		final NativeObservable observable = 
				ObservableManager.getObservable(AppObservable.class);
		if (observable != null) {
			observable.deleteObserver(mAppObserver);
		}
	}

	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			if (arg1 instanceof Intent == false) {
				return;
			}
			
			restartLoader();
		}
		
	};
	

}
