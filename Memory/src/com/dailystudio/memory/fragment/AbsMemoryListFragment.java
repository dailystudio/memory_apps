package com.dailystudio.memory.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.dailystudio.app.fragment.AbsCursorAdapterFragment;
import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.memory.ui.ResourceObjectAdapter;

public abstract class AbsMemoryListFragment extends AbsCursorAdapterFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Intent intent = getActivity().getIntent();
		
		bindIntent(intent);

		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	protected void bindAdapterView() {
		ListAdapter oldAdapter = getAdapter();
		if (oldAdapter instanceof ResourceObjectAdapter<?>) {
			ThumbCacheManager.removeCacheObserver(
					(ResourceObjectAdapter<?>)oldAdapter);
		}
		
		super.bindAdapterView();
		
		ListAdapter newAdapter = getAdapter();
		if (newAdapter != null) {
			ThumbCacheManager.addCacheObserver(
					(ResourceObjectAdapter<?>)newAdapter);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ListAdapter oldAdapter = getAdapter();
		if (oldAdapter instanceof ResourceObjectAdapter<?>) {
			ThumbCacheManager.removeCacheObserver(
					(ResourceObjectAdapter<?>)oldAdapter);
		}
	}
	
}
