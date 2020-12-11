package com.dailystudio.memory.where.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspotDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdentifiedLocationObject;

public class IdentifiedHotspotListActivity extends AbsLocationListActivity {

	private int mQuestionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_idspot_list);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		mQuestionId = intent.getIntExtra(Constants.EXTRA_QUESTION_ID, 
				Constants.INVALID_ID);
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Intent i = getIntent();
		
		Logger.debug("action = %s", i.getAction());
		
		Fragment fragment = getPagerFragment();
		if (fragment != null) {
			FragmentTransaction ft = 
					getSupportFragmentManager().beginTransaction();
			if (i.getAction() == Intent.ACTION_PICK) {
				Logger.debug("hide pager fragment[%s] for PICK action", fragment);
				ft.hide(fragment);
			} else {
				Logger.debug("show pager fragment[%s] for rest action", fragment);
				ft.show(fragment);
			}
			
			ft.commit();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Logger.debug("mQuestionId = %d", mQuestionId);
		if (mQuestionId != Constants.INVALID_ID) {
			MemoryAsk.answerQuestion(getApplicationContext(), 
					mQuestionId, "reviewed");
		}
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_idspot_list, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_list_weeks: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), WeeksListActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
			
		}
		
		return super.onOverflowItemSelected(item);
	}

	@Override
	protected BaseLocationObject dumpLocationObject(Object data) {
		if (data instanceof IdentifiedLocationObject == false) {
			return null;
		}
		
		return (IdentifiedLocationObject)data;
	}
	
	@Override
	protected Intent locationObjectToViewIntent(BaseLocationObject locObject) {
		if (locObject instanceof IdentifiedLocationObject == false) {
			return super.locationObjectToViewIntent(locObject);
		}
		
		Intent i = super.locationObjectToViewIntent(locObject);
		if (i == null) {
			return i;
		}
		
		IdentifiedHotspot idspot = 
				IdentifiedHotspotDatabaseModal.getIdentifiedHotspot(
						this, locObject);
		Logger.debug("idspot = %s", idspot);
		if (idspot != null) {
			final int idspotId = idspot.getId();

			i.putExtra(Constants.EXTRA_IDSPOT_ID, idspotId);
		}
		
		i.setClass(getApplicationContext(), IdentifiedHotspotMapActivity.class);
       
        return i;
	}
	
	private Fragment getPagerFragment() {
		return getSupportFragmentManager().findFragmentById(
				R.id.idspot_stat_pager_fragment);
	}

}
