package com.dailystudio.memory.fragment;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.dailystudio.app.fragment.AbsCursorAdapterFragment;
import com.dailystudio.app.ui.AnimatedImageView;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.MemoryKeynoteCacheService;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.KeyNoteCachesLoader;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.lifestyle.TimeSpan;
import com.dailystudio.memory.plugin.PluginObserverable;
import com.dailystudio.memory.ui.KeynoteCachesAdapter;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class KeyNotesListFragment extends AbsCursorAdapterFragment {
	
	private AnimatedImageView mDogAnimView;

	private long mKeynotesStart = -1;
	private long mKeynotesEnd = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			mKeynotesStart = bundle.getLong(Constants.EXTRA_PEROID_START,
					-1);
			mKeynotesEnd = bundle.getLong(Constants.EXTRA_PEROID_END,
					-1);
			
			Logger.debug("keynotes fragment: [%s - %s]",
					mKeynotesStart, mKeynotesEnd);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_keynotes, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		mDogAnimView = (AnimatedImageView) fragmentView.findViewById(
				android.R.id.empty);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mDogAnimView != null) {
			mDogAnimView.playAnimation();
		}
		
		requestUpdateCaches();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mDogAnimView != null) {
			mDogAnimView.stopAnimation();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0,
			Bundle arg1) {
		return new KeyNoteCachesLoader(getActivity(), mKeynotesStart, mKeynotesEnd);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new KeynoteCachesAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_KEY_NOTES_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//		super.onItemClick(l, v, position, id);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		registerReceivers();
		registerObservers();
		
		requestUpdateCaches();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		unregisterReceivers();
		unregisterObservers();
	}

	private void requestUpdateCaches() {
		final Context context = getActivity();
		if (context == null) {
			return;
		}
		
		Intent updateIntent = new Intent(
				MemoryKeynoteCacheService.ACTION_UPDATE_KEYNOTES_CACHES);
		
		updateIntent.setClass(context.getApplicationContext(),
				MemoryKeynoteCacheService.class);
		
		updateIntent.putExtra(Constants.EXTRA_PEROID_START, mKeynotesStart);
		updateIntent.putExtra(Constants.EXTRA_PEROID_END, mKeynotesEnd);

		Logger.debug("updateIntent = %s", updateIntent);
		
		context.startService(updateIntent);
	}

	private void registerReceivers() {
		IntentFilter filter = new IntentFilter();
		
		
		filter.addAction(Constants.ACTION_MEMORY_KEYNOTE_UPDATED);
		filter.addAction(MemoryKeynoteCacheService.ACTION_KEYNOTES_CACHES_UPDATED);
		
		try {
			getActivity().registerReceiver(mKeynotesUpdatedReceiver, filter);
		} catch (Exception e) {
			Logger.warnning("could not register card updated receiver: %s",
					e.toString());
		}
	}

	private void unregisterReceivers() {
		try {
			getActivity().unregisterReceiver(mKeynotesUpdatedReceiver);
		} catch (Exception e) {
			Logger.warnning("could not unregister card updated receiver: %s",
					e.toString());
		}
	}

	private void registerObservers() {
		NativeObservable observable = 
			ObservableManager.getObservable(PluginObserverable.class);
		
		if (observable != null) {
			observable.addObserver(mPluginsObserver);
		}
	}

	private void unregisterObservers() {
		
		NativeObservable observable = 
			ObservableManager.getObservable(PluginObserverable.class);
		
		if (observable != null) {
			observable.deleteObserver(mPluginsObserver);
		}
	}

	private BroadcastReceiver mKeynotesUpdatedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			
			final String action = intent.getAction();
			Logger.debug("keynote action: %s", action);
			if (Constants.ACTION_MEMORY_KEYNOTE_UPDATED.equals(action)) {
//				restartLoader();
				requestUpdateCaches();
			} else if (MemoryKeynoteCacheService.ACTION_KEYNOTES_CACHES_UPDATED.equals(action)) {
				restartLoader();
			}
		}
	};

	private Observer mPluginsObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};
	
	public static Fragment newInstance(Context context, TimeSpan kp) {
		if (context == null || kp == null) {
			return null;
		}

		Bundle args = new Bundle();
		args.putLong(Constants.EXTRA_PEROID_START, kp.start);
		args.putLong(Constants.EXTRA_PEROID_END, kp.end);

		Fragment frag = Fragment.instantiate(context,
				KeyNotesListFragment.class.getName(), args);

		return frag;
	}

}
