package com.dailystudio.memory.fragment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.app.fragment.AbsArrayAdapterFragment;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.CardsLoader;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.plugin.PluginObserverable;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.ui.CardsAdapter;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;


public class CardListFragment extends AbsArrayAdapterFragment<MemoryPieceCard> {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_card_list, null);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		registerReceivers();
		registerObservers();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		unregisterReceivers();
		unregisterObservers();
	}
	
	private void registerReceivers() {
		IntentFilter filter = new IntentFilter(Constants.ACTION_MEMORY_CARD_UPDATED);
		
		try {
			getActivity().registerReceiver(mCardUpdatedReceiver, filter);
		} catch (Exception e) {
			Logger.warnning("could not register card updated receiver: %s",
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

	private void unregisterReceivers() {
		try {
			getActivity().unregisterReceiver(mCardUpdatedReceiver);
		} catch (Exception e) {
			Logger.warnning("could not unregister card updated receiver: %s",
					e.toString());
		}
	}

	@Override
	public Loader<List<MemoryPieceCard>> onCreateLoader(int arg0,
			Bundle arg1) {
		return new CardsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new CardsAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_CARDS_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}
	
	private BroadcastReceiver mCardUpdatedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			
			final String action = intent.getAction();
			Logger.debug("card action: %s", action);
			if (Constants.ACTION_MEMORY_CARD_UPDATED.equals(action)) {
				final String cardName = intent.getStringExtra(
						Constants.EXTRA_CARD_NAME);
				
				Logger.debug("cardName: %s", cardName);
				getAdapter().notifyDataSetChanged();
			}
		}
	};

	private Observer mPluginsObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};

}
