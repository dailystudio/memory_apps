package com.dailystudio.memory.person;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.person.databaseobject.PersonFeatureDatabaseModal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class PersonFeatureQueryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}
		
		final String action = intent.getAction();
		if (Constants.ACTION_MEMORY_PERSON_GET_FEATURE.equals(action)) {
			final String pid = intent.getStringExtra(Constants.EXTRA_PERSON_ID);
			final String fid = intent.getStringExtra(Constants.EXTRA_FEATURE_ID);
			
			final String fVal = PersonFeatureDatabaseModal.getFeature(
					context, pid, fid);
			Logger.debug("pid = %s, fid = %s [val: %s]", 
					pid, fid, fVal);
			
			if (!TextUtils.isEmpty(fVal)) {
				Bundle resultExtras = getResultExtras(true);

				if (resultExtras != null) {
					resultExtras.putString(Constants.EXTRA_PERSON_ID,
							pid);
					resultExtras.putString(Constants.EXTRA_FEATURE_ID,
							fid);
					resultExtras.putString(Constants.EXTRA_FEATURE_VAL,
							fVal);
				}
			}
		}
	}

}
