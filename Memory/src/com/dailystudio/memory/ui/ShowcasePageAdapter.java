package com.dailystudio.memory.ui;

import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.fragment.ShowcaseFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ShowcasePageAdapter extends FragmentStatePagerAdapter {
	
	private Context mContext;
	
	private List<ShowcasePage> mShowcasePages;
	
	public ShowcasePageAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mShowcasePages == null) {
			return 0;
		}
		
		return mShowcasePages.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (mShowcasePages == null) {
			return null;
		}
		
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		final ShowcasePage sp = mShowcasePages.get(position);
		Logger.debug("sp = %s", sp);
		if (sp == null) {
			return null;
		}
		
		Fragment fragment = 
			ShowcaseFragment.newInstance(mContext, sp);
		
		return fragment;
	}
	
	public void setShowpages(List<ShowcasePage> pages) {
		mShowcasePages = pages;
		
		notifyDataSetChanged();
	}

}
