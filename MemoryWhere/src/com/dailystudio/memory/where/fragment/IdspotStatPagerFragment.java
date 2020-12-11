package com.dailystudio.memory.where.fragment;

import java.util.List;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.loader.IdspotStatPagesLoader;
import com.dailystudio.memory.where.ui.IdspotStatPagerAdapter;
import com.dailystudio.memory.where.ui.IdspotStatPage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IdspotStatPagerFragment extends AbsLoaderFragment<List<IdspotStatPage>> {
	
	private ViewPager mViewPager;
	private IdspotStatPagerAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_idspot_stat_pager, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mViewPager = (ViewPager) fragmentView.findViewById(R.id.stat_pager);
		if (mViewPager != null) {
			final Activity activity = getActivity();
			if (activity instanceof FragmentActivity) {
				mAdapter = new IdspotStatPagerAdapter(getActivity(), 
						((FragmentActivity)activity).getSupportFragmentManager());
				
				mViewPager.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public Loader<List<IdspotStatPage>> onCreateLoader(int arg0, Bundle arg1) {
		return new IdspotStatPagesLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<IdspotStatPage>> loader,
			List<IdspotStatPage> data) {
		if (mAdapter != null) {
			mAdapter.setStatPages(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<IdspotStatPage>> loader) {
		if (mAdapter != null) {
			mAdapter.setStatPages(null);
		}
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_IDSPOT_STAT_PAGES_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
