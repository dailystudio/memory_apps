package com.dailystudio.memory.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.dailystudio.app.fragment.AbsCursorAdapterFragment;
import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.MemorySearchCursorLoader;
import com.dailystudio.memory.ui.MemorySearchResultAdapter;

public class SearchResultsListFragment extends AbsCursorAdapterFragment {

	private String mQueryInput;
	private String mQueryAuthority;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loadId, Bundle args) {
		if (args == null) {
			return null;
		}
		
		String queryInput = 
				args.getString(Constants.EXTRA_SEARCH_INPUT);
		
		return new MemorySearchCursorLoader(getActivity(), 
				queryInput,
				mQueryAuthority);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new MemorySearchResultAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_SEARCH_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		args.putString(Constants.EXTRA_SEARCH_INPUT, mQueryInput);
		
		return args;
	}
	
	@Override
	protected void bindAdapterView() {
		ListAdapter oldAdapter = getAdapter();
		if (oldAdapter instanceof MemorySearchResultAdapter) {
			ThumbCacheManager.removeCacheObserver(
					(MemorySearchResultAdapter)oldAdapter);
		}
		
		super.bindAdapterView();
		
		ListAdapter newAdapter = getAdapter();
		if (newAdapter instanceof MemorySearchResultAdapter) {
			ThumbCacheManager.addCacheObserver(
					(MemorySearchResultAdapter)newAdapter);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ListAdapter oldAdapter = getAdapter();
		if (oldAdapter instanceof MemorySearchResultAdapter) {
			ThumbCacheManager.removeCacheObserver(
					(MemorySearchResultAdapter)oldAdapter);
		}
	}

	
	public void doSearchForInput(String input, String authority) {
		mQueryInput = input;
		mQueryAuthority = authority;
		
		restartLoader();
	}

}
