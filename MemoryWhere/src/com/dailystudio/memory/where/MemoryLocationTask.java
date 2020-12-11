package com.dailystudio.memory.where;

import android.content.Context;
import android.content.Intent;

import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.ask.MemoryQuestion;
import com.dailystudio.memory.task.Task;
import com.dailystudio.memory.where.activity.CurrentIdspotListActivity;
import com.dailystudio.memory.where.card.Cards;
import com.dailystudio.memory.where.card.WhereCardsUpdater;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspotDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;
import com.dailystudio.memory.where.databaseobject.MemoryLocationModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;
import com.dailystudio.memory.where.locationapi.AbsLocationApi;
import com.dailystudio.memory.where.locationapi.LocationApiManager;

public class MemoryLocationTask extends Task {
	
	private MemoryLocation mLastLocation = null;
	private IdentifiedHotspot mLastIdspot = null;

	private AbsLocationApi mApi = null;

	public MemoryLocationTask(Context context) {
		super(context);

//		mApi = new GoogleLocationApi(context);
		mApi = LocationApiManager.getDefaultApi(context);
	}

	@Override
	public void onCreate(Context context, long time) {
		if (context == null) {
			return;
		}

		if (mApi != null) {
			mApi.startTracking();
		}
	}

	@Override
	public void onDestroy(Context context, long time) {
		if (mApi != null) {
			mApi.stopTracking();
		}
	}

	@Override
	public void onExecute(Context context, long time) {
		if (mApi == null) {
			return;
		}

		MemoryLocation location = mApi.getCurrentLocation();
		if (location == null) {
			return;
		}
		
		if (mLastLocation == null) {
			mLastLocation = MemoryLocationModal.queryLastLocation(mContext);
		}
		
		Logger.debug("curr(%s)", location);
		Logger.debug("prev(%s)", mLastLocation);
		Logger.debug("distance(%f)", 
				MemoryLocation.getDistanceBetween(location, mLastLocation));

		boolean nearby = (location.isNearBy(mLastLocation, Constants.NEARY_BY_THRESHOLD));
		Logger.debug("nearby(%s, threshold: %d)", nearby, Constants.NEARY_BY_THRESHOLD);

		long timeInc = 0;
		
		if (mLastLocation != null) {
			timeInc = location.getTime() - 
				(mLastLocation.getTime() + mLastLocation.getDuration());
			
			if (timeInc <= 0) {
				timeInc = time - 
					(mLastLocation.getTime() + mLastLocation.getDuration());
			}
		}
		
		Logger.debug("timeInc(%d)", timeInc);
		
		TimeCapsuleDatabaseWriter<MemoryLocation> writer =
				new TimeCapsuleDatabaseWriter<MemoryLocation>(context, 
						MemoryLocation.class);
		
		if (timeInc > 0 && mLastLocation != null) {
			mLastLocation.setDuration(mLastLocation.getDuration() + timeInc);
			
			writer.update(mLastLocation);
		}
		
		if (nearby == false) {
			long rowId = writer.insert(location);
			Logger.debug("rowId(%d)", rowId);
			if (rowId <= 0) {
				return;
			}
			
			location.setId((int)rowId);
			mLastLocation = location;
		}
		
		createOrUpdateIdspotHistory(context, 
				(mLastLocation == null ? location : mLastLocation));
	}

	private void createOrUpdateIdspotHistory(Context context,
			MemoryLocation location) {
		Logger.debug("location = %s", location);
		if (context == null || location == null) {
			return;
		}
		
		IdentifiedHotspot idspot = null;
		
		idspot = IdentifiedHotspotDatabaseModal.findMatchedIdentifiedHotspot(
					context, location);
		Logger.debug("idspot = %s", idspot);
		Logger.debug("mLastIdspot = %s", mLastIdspot);
			
		if (idspot == null) {
			if (mLastIdspot != null) {
				IdspotHistoryDatabaseModal.markLeaveIdspot(context);
				MemoryAsk.answerQuestion(context, 
						QuestionIds.QUESTION_WELCOME_IDSPOT, "leaved");
			}
			
			updateNowhereCard();
			
			mLastIdspot = null;
			
			return;
		}
		
		boolean newIdspot = 
				IdspotHistoryDatabaseModal.markEnterIdspot(context, idspot);
		
		if (newIdspot) {
			askWelcomeToIdspot(context, idspot);
			
			updateNowhereCard();
		}
		
		mLastIdspot = idspot;
	}
	
	private void askWelcomeToIdspot(Context context, IdentifiedHotspot idspot) {
		if (context == null || idspot == null) {
			return;
		}
		
		Intent askIntent = new Intent();
		
		askIntent.setClass(context.getApplicationContext(), 
				CurrentIdspotListActivity.class);
		
		askIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		askIntent.putExtra(Constants.EXTRA_LATITUDE, idspot.getLatitude());
//		askIntent.putExtra(Constants.EXTRA_LONGITUDE, idspot.getLongitude());
		
		final HotspotIdentity identity = idspot.getIdentity();
		if (identity == null) {
			return;
		}
		
		final HotspotIdentityInfo hInfo = HotspotIdentifier.getIdentityInfo(identity);
		if (hInfo == null) {
			return;
		}
		
		final String qtempl = mContext.getString(R.string.welcome_idspot_ask_templ);
		if (qtempl == null) {
			return;
		}
		
		final String qtext = String.format(qtempl, 
				context.getString(hInfo.labelResId));
		
		MemoryAsk.askQuestion(context, 
				QuestionIds.QUESTION_WELCOME_IDSPOT, 
				qtext, 
				MemoryQuestion.PRIORITY_EMERGENT,
				askIntent);
	}
	
	private void updateNowhereCard() {
		new WhereCardsUpdater(mContext, Cards.CARD_IDSPOT_NOW).doUpdate();
	}

}
