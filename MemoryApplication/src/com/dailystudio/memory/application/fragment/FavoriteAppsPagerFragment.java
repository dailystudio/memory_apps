package com.dailystudio.memory.application.fragment;

import java.util.List;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.loader.FavoriteAppClassesLoader;
import com.dailystudio.memory.application.ui.FavoriteAppsPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavoriteAppsPagerFragment extends AbsLoaderFragment<List<Integer>> {
	
	private ViewPager mViewPager;
	private FavoriteAppsPagerAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_favorite_apps_pager, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mViewPager = (ViewPager) fragmentView.findViewById(R.id.fapp_list_pager);
		if (mViewPager != null) {
			final Activity activity = getActivity();
			if (activity instanceof FragmentActivity) {
				mAdapter = new FavoriteAppsPagerAdapter(getActivity(), 
						((FragmentActivity)activity).getSupportFragmentManager());
				
				mViewPager.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public Loader<List<Integer>> onCreateLoader(int arg0, Bundle arg1) {
		return new FavoriteAppClassesLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Integer>> loader,
			List<Integer> data) {
		if (mAdapter != null) {
			mAdapter.setFavoriteClasses(data);

			if (mViewPager != null && data != null) {
				final int fclass = FavoriteApp.timeToFavoriteClass(
						System.currentTimeMillis());

				final int N = data.size();
				int c = Constants.FAVORITE_CLASS_WEEK;
				int index;
				for (index = 0; index <= N; index++) {
					c = data.get(index);
					if (c == fclass) {
						break;
					}
				}
				
				if (index < N) {
					mViewPager.setCurrentItem(index, false);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Integer>> loader) {
		if (mAdapter != null) {
			mAdapter.setFavoriteClasses(null);
		}
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_FAVORITE_APPS_CLASSES;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
