package com.dailystudio.memory.where.ui;

import java.util.List;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class IdspotStatPagerAdapter extends FragmentStatePagerAdapter {
	
	private Context mContext;
	
	private List<IdspotStatPage> mStatPages;
	
	public IdspotStatPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mStatPages == null) {
			return 0;
		}
		
		return mStatPages.size();
	}

	@Override
	public Fragment getItem(int position) {
		if (mStatPages == null) {
			return null;
		}
		
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		final IdspotStatPage sp = mStatPages.get(position);
		Logger.debug("sp = %s", sp);
		if (sp == null) {
			return null;
		}
		
		Fragment fragment = Fragment.instantiate(mContext, 
				sp.fragmentName);
		
		return fragment;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (mStatPages == null) {
			return null;
		}
		
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		final IdspotStatPage sp = mStatPages.get(position);
		Logger.debug("sp = %s", sp);
		if (sp == null) {
			return null;
		}
		
		return mContext.getString(sp.labelResId);
	}
	
	public void setStatPages(List<IdspotStatPage> pages) {
		mStatPages = pages;
		
		notifyDataSetChanged();
	}

}
