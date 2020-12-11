package com.dailystudio.memory.fragment;

import java.util.List;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.ShowcasePagesLoader;
import com.dailystudio.memory.ui.ShowcasePage;
import com.dailystudio.memory.ui.ShowcasePageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShowcasePagerFragment extends AbsLoaderFragment<List<ShowcasePage>> {
	
	private ViewPager mViewPager;
	private ShowcasePageAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_showcase_pager, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mViewPager = (ViewPager) fragmentView.findViewById(R.id.showcase_pager);
		if (mViewPager != null) {
			final Activity activity = getActivity();
			if (activity instanceof FragmentActivity) {
				mAdapter = new ShowcasePageAdapter(getActivity(), 
						((FragmentActivity)activity).getSupportFragmentManager());
				
				mViewPager.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public Loader<List<ShowcasePage>> onCreateLoader(int arg0, Bundle arg1) {
		return new ShowcasePagesLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<ShowcasePage>> loader,
			List<ShowcasePage> data) {
		if (mAdapter != null) {
			mAdapter.setShowpages(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<ShowcasePage>> loader) {
		if (mAdapter != null) {
			mAdapter.setShowpages(null);
		}
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_SHOWCASE_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
