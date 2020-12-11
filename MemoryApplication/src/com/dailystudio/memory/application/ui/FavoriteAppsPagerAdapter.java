package com.dailystudio.memory.application.ui;

import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.fragment.FavoriteAppsListFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FavoriteAppsPagerAdapter extends FragmentStatePagerAdapter {
	
	private Context mContext;
	private List<Integer> mFavoriteClasses;

	public FavoriteAppsPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mFavoriteClasses == null) {
			return 0;
		}
		
		return mFavoriteClasses.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (mFavoriteClasses == null) {
			return null;
		}
		
		if (position < 0 || position >= mFavoriteClasses.size()) {
			return null;
		}
		
		final int favoriteClass = mFavoriteClasses.get(position);
		Logger.debug("favoriteClass = %d", favoriteClass);
		
		Fragment fragment = 
			FavoriteAppsListFragment.newInstance(mContext,
					favoriteClass, false);
		
		return fragment;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (mFavoriteClasses == null) {
			return super.getPageTitle(position);
		}
		
		if (position < 0 || position >= mFavoriteClasses.size()) {
			return super.getPageTitle(position);
		}
		
		final int favoriteClass = mFavoriteClasses.get(position);
		final String title = 
				FavoriteApp.favoriteClassToLabel(mContext, favoriteClass);
				
		Logger.debug("favoriteClass = %d, title = %s",
				favoriteClass, title);

		return title;
	}
	
	public void setFavoriteClasses(List<Integer> classes) {
		mFavoriteClasses = classes;
		
		notifyDataSetChanged();
	}
	
}
