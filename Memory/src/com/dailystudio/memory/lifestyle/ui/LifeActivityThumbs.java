package com.dailystudio.memory.lifestyle.ui;

import java.util.HashMap;
import java.util.Map;

import com.dailystudio.memory.R;
import com.dailystudio.memory.person.PersonFeatures;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class LifeActivityThumbs {
	
	private final static Map<String, Integer> sActivityResources = 
			new HashMap<String, Integer>();
	private final static Map<String, Drawable> sActivityThumbs = 
			new HashMap<String, Drawable>();
	
	static {
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_GETUP,
				R.drawable.day_life_gettingup);
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_SLEEP,
				R.drawable.day_life_sleeping);
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START,
				R.drawable.day_life_working);
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END,
				R.drawable.day_life_working);
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START,
				R.drawable.day_life_eating);
		sActivityResources.put(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END,
				R.drawable.day_life_eating);
	}
	
	public static Drawable getLifeActivityThumb(Context context, 
			String feature) {
		if (context == null 
				|| TextUtils.isEmpty(feature)) {
			return null;
		}
		
		Drawable d = sActivityThumbs.get(feature);
		if (d == null) {
			if (!sActivityResources.containsKey(feature)) {
				return null;
			}
			
			d = context.getResources().getDrawable(
					sActivityResources.get(feature));
			if (d != null) {
				sActivityThumbs.put(feature, d);
			}
		}
		
		return d;
	}

}
