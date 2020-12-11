package com.dailystudio.memory.ui;

import com.dailystudio.memory.fragment.MessSpaceFragment;
import com.dailystudio.memory.fragment.PluginActivityCategoryListFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

	private static final Class<?>[] MAIN_FRAGMENT_CLASSES = {
		PluginActivityCategoryListFragment.class,
		MessSpaceFragment.class,
	};
	
	protected Context mContext;

	public MainPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		return Fragment.instantiate(mContext, 
				MAIN_FRAGMENT_CLASSES[position].getName());
	}

	@Override
	public int getCount() {
		return MAIN_FRAGMENT_CLASSES.length;
	}

}
