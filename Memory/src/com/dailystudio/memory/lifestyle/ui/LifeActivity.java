package com.dailystudio.memory.lifestyle.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.person.PersonFeatureBundle;

public class LifeActivity {

	private PersonFeatureBundle mStartBundle;
	private PersonFeatureBundle mEndBundle;
	
	private boolean mIsStartMain;
	private int mLabelResId;
	
	public LifeActivity(int labelResId, PersonFeatureBundle start, PersonFeatureBundle end) {
		this(labelResId, start, end, true);
	}
	
	public LifeActivity(int labelResId, PersonFeatureBundle start, PersonFeatureBundle end, 
			boolean startIsMain) {
		mStartBundle = start;
		mEndBundle = end;
		
		mIsStartMain = startIsMain;
		
		mLabelResId = labelResId;
	}
	
	public CharSequence getLabel(Context context) {
		if (context == null) {
			return null;
		}
		
		return context.getString(mLabelResId);
	}
	
	private Drawable getBundleDrawable(Context context,
			PersonFeatureBundle bundle) {
		if (context == null || bundle == null) {
			return null;
		}
		
		return LifeActivityThumbs.getLifeActivityThumb(context, 
				bundle.getFeatureId());
	}
	
	public Drawable getMainDrawable(Context context) {
		return getBundleDrawable(context, 
				mIsStartMain ? mStartBundle : mEndBundle);
	}

	public Drawable getStartDrawable(Context context) {
		return getBundleDrawable(context, mStartBundle);
	}
	
	public Drawable getEndDrawable(Context context) {
		return getBundleDrawable(context, mEndBundle);
	}
	
	public long getTime(PersonFeatureBundle bundle) {
		if (bundle == null) {
			return 0l;
		}
		
		final String valstr = bundle.getFeatureValue();
		if (TextUtils.isEmpty(valstr)) {
			return 0l;
		}
		
		long timeval = 0l;
		try {
			timeval = Long.parseLong(valstr);
		} catch (Exception e) {
			Logger.warnning("could not parse time from bundle[%s]: %s",
					bundle, e.toString());
			
			timeval = 0l;
		}
		
		return timeval;
	}
	
	public long getStartTime() {
		return getTime(mStartBundle);
	}
	
	public long getEndTime() {
		return getTime(mEndBundle);
	}
	
}
