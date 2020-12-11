package com.dailystudio.memory.person;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.nativelib.observable.NativeObservable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PersonFeatureObservable extends NativeObservable {

	public PersonFeatureObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
		IntentFilter filter = new IntentFilter(
				PersonFeatureUpdateService.ACTION_PERSON_FEATURE_UPDATED);
		
		try {
			mContext.registerReceiver(mPersonFeaturesUpdatedReceiver, filter);
		} catch (Exception e) {
			Logger.warnning("could not register person feature updated receiver: %s",
					e.toString());
		}
	}

	@Override
	protected void onDestroy() {
		
		try {
			mContext.unregisterReceiver(mPersonFeaturesUpdatedReceiver);
		} catch (Exception e) {
			Logger.warnning("could not unregister person feature updated receiver: %s",
					e.toString());
		}
	}

	@Override
	protected void onPause() {
	}

	@Override
	protected void onResume() {
	}
	
	private BroadcastReceiver mPersonFeaturesUpdatedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			
			final String action = intent.getAction();
			if (PersonFeatureUpdateService.ACTION_PERSON_FEATURE_UPDATED.equals(action)) {
				notifyObservers(intent.getStringExtra(Constants.EXTRA_PERSON_ID));
			}
		}
		
	};
	
}
