package com.dailystudio.memory.person;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.person.databaseobject.PersonFeatureDatabaseModal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;

public class PersonFeatureUpdateService extends IntentService {

	private final static String SERVICE_NAME = "person-feature-service";

	public final static String ACTION_PERSON_FEATURE_UPDATED = 
			"memory.intent.ACTION_PERSON_FEATURE_UPDATED";

	public PersonFeatureUpdateService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Logger.debug("intent = %s", intent);
		if (intent == null) {
			return;
		}
		
		final String action = intent.getAction();
		if (Constants.ACTION_MEMORY_PERSON_SET_FEATURE.equals(action)) {
			final String pid = intent.getStringExtra(Constants.EXTRA_PERSON_ID);
			final String fid = intent.getStringExtra(Constants.EXTRA_FEATURE_ID);
			final String val = intent.getStringExtra(Constants.EXTRA_FEATURE_VAL);
			Logger.debug("pid = %s, fid = %s, val = %s",
					pid, fid, val);

			syncPersonFeature(getApplicationContext(), pid, fid, val);
		} else if (Constants.ACTION_MEMORY_PERSON_SET_FEATURES.equals(action)) {
			final String pid = intent.getStringExtra(Constants.EXTRA_PERSON_ID);
			final Parcelable[] parcelables = intent.getParcelableArrayExtra(
					Constants.EXTRA_FEATURES);
			
			final List<PersonFeatureBundle> bundles = 
					toBundleList(parcelables);
			Logger.debug("pid = %s, bundles = %s",
					pid, bundles);

			syncPersonFeatures(getApplicationContext(), pid, bundles);
		}
	}

	private void syncPersonFeature(Context context, String pid, String fid,
			String value) {
		if (context == null
				|| TextUtils.isEmpty(pid)
				|| TextUtils.isEmpty(fid)) {
			return;
		}
		
		PersonFeatureDatabaseModal.setFeature(context, pid, fid, value);
		
		notifyFeaturesUpdate(pid);
	}
	
	private void syncPersonFeatures(Context context, String pid, 
			List<PersonFeatureBundle> bundles) {
		if (context == null
				|| TextUtils.isEmpty(pid)
				|| bundles == null
				|| bundles.size() <= 0) {
			return;
		}
		
		PersonFeatureDatabaseModal.setFeatures(context, pid, bundles);
		
		notifyFeaturesUpdate(pid);
	}
	
	private List<PersonFeatureBundle> toBundleList(Parcelable[] bundles) {
		if (bundles == null
				|| bundles.length <= 0) {
			return null;
		}
		
		List<PersonFeatureBundle> list = new ArrayList<PersonFeatureBundle>();
		for (Parcelable p: bundles) {
			list.add((PersonFeatureBundle)p);
		}
		
		return list;
	}

	private void notifyFeaturesUpdate(String person) {
		if (TextUtils.isEmpty(person)) {
			return;
		}
		
		Intent notifyIntent = new Intent(ACTION_PERSON_FEATURE_UPDATED);
		
		notifyIntent.putExtra(Constants.EXTRA_PERSON_ID, person);
		
		getApplicationContext().sendBroadcast(notifyIntent);
	}

}
