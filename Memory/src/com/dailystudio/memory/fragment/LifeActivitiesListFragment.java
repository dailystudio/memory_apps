package com.dailystudio.memory.fragment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.app.fragment.AbsArrayAdapterFragment;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LifeActivitiesLoader;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.lifestyle.ui.LifeActivitiesAdapter;
import com.dailystudio.memory.lifestyle.ui.LifeActivity;
import com.dailystudio.memory.person.PersonFeatureObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class LifeActivitiesListFragment extends AbsArrayAdapterFragment<LifeActivity> {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		registerObservers();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		unregisterObservers();
	}
	
	private void registerObservers() {
		NativeObservable observable = 
				ObservableManager.getObservable(PersonFeatureObservable.class);
		if (observable != null) {
			observable.addObserver(mObserver);
		}
	}

	private void unregisterObservers() {
		NativeObservable observable = 
				ObservableManager.getObservable(PersonFeatureObservable.class);
		if (observable != null) {
			observable.deleteObserver(mObserver);
		}
	}

	@Override
	public Loader<List<LifeActivity>> onCreateLoader(int arg0,
			Bundle arg1) {
		return new LifeActivitiesLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new LifeActivitiesAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_LIFESTYLE_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}
	
	private Observer mObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};

}
