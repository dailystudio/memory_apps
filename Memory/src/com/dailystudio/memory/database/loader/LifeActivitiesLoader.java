package com.dailystudio.memory.database.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.R;
import com.dailystudio.memory.lifestyle.ui.LifeActivity;
import com.dailystudio.memory.person.PersonFeatureBundle;
import com.dailystudio.memory.person.PersonFeatures;
import com.dailystudio.memory.person.PersonInformation;
import com.dailystudio.memory.person.Persons;

public class LifeActivitiesLoader extends AbsAsyncDataLoader<List<LifeActivity>> {
	
	private final static String[] FEATURES = {
		PersonFeatures.FEATURE_LIFE_STYLE_GETUP,
		PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START,
		PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START,
		PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END,
		PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END,
		PersonFeatures.FEATURE_LIFE_STYLE_SLEEP,
	};

	public LifeActivitiesLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<LifeActivity> loadInBackground() {
		final PersonInformation pi = 
				new PersonInformation(getContext(),
						Persons.PERSON_ME);
		
		Map<String, PersonFeatureBundle> bundles = 
				new HashMap<String, PersonFeatureBundle>();
		
		String fVal = null;
		for (String feature: FEATURES) {
			fVal = pi.getFeature(feature);
			
			bundles.put(feature, new PersonFeatureBundle(feature,
					fVal));
		}
		
		if (bundles.size() <= 0) {
			return null;
		}
		
		List<LifeActivity> activities = new ArrayList<LifeActivity>();
		
		LifeActivity activity = null;
		
		activity = createActivity(
				R.string.life_activity_getup,
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_GETUP), 
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_GETUP));
		if (activity != null) {		
			activities.add(activity);
		}
		
		activity = createActivity(
				R.string.life_activity_working,
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_START), 
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START));
		if (activity != null) {		
			activities.add(activity);
		}
		
		activity = createActivity(
				R.string.life_activity_lunch,
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_START), 
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END));
		if (activity != null) {		
			activities.add(activity);
		}
		
		activity = createActivity(
				R.string.life_activity_working,
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_LUNCH_END), 
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_WORKTIME_END),
				false);
		if (activity != null) {		
			activities.add(activity);
		}
		
		activity = createActivity(
				R.string.life_activity_sleep,
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_SLEEP), 
				bundles.get(PersonFeatures.FEATURE_LIFE_STYLE_GETUP));
		if (activity != null) {		
			activities.add(activity);
		}
		
		return activities;
	}
	
	private LifeActivity createActivity(int label, PersonFeatureBundle start,
			PersonFeatureBundle end) {
		return createActivity(label, start, end, true);
	}
	
	private LifeActivity createActivity(int label, PersonFeatureBundle start,
			PersonFeatureBundle end,
			boolean isStartMain) {
		if (start == null || end == null) {
			return null;
		}
		
		final LifeActivity activity = 
				new LifeActivity(label, start, end, isStartMain);
		if (activity.getStartTime() <= 0
				|| activity.getEndTime() <= 0) {
			return null;
		}
		
		return activity;
	}

}
