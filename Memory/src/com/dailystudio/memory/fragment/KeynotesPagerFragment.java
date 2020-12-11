package com.dailystudio.memory.fragment;

import java.util.List;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.KeynotesPagesLoader;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.lifestyle.TimeSpan;
import com.dailystudio.memory.ui.KeynotesPageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class KeynotesPagerFragment extends AbsLoaderFragment<List<TimeSpan>> {
	
	private ViewPager mViewPager;
	private KeynotesPageAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_keynotes_pager, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mViewPager = (ViewPager) fragmentView.findViewById(R.id.keynotes_pager);
		if (mViewPager != null) {
			final Activity activity = getActivity();
			if (activity instanceof FragmentActivity) {
				mAdapter = new KeynotesPageAdapter(getActivity(), 
						((FragmentActivity)activity).getSupportFragmentManager());
				
				mViewPager.setAdapter(mAdapter);
				mViewPager.setOffscreenPageLimit(2);
			}
		}
	}
	
	@Override
	public Loader<List<TimeSpan>> onCreateLoader(int arg0, Bundle arg1) {
		return new KeynotesPagesLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<TimeSpan>> loader,
			List<TimeSpan> data) {
		if (mAdapter != null) {
			mAdapter.setTimespans(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<TimeSpan>> loader) {
		if (mAdapter != null) {
			mAdapter.setTimespans(null);
		}
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_KEY_NOTES_PAGE_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}

}
