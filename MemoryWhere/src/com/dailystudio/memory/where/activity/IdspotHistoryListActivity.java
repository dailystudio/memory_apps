package com.dailystudio.memory.where.activity;

import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.fragment.IdspotHistoryListFragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class IdspotHistoryListActivity extends AbsLocationListActivity {

	private int mIdspotId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_idspot_history_list);
		
		setupActionBar();
	}
	
	private void setupActionBar() {
	}

	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);

		mIdspotId = intent.getIntExtra(Constants.EXTRA_IDSPOT_ID, -1);
		if (mIdspotId != -1) {
			IdspotHistoryListFragment fragment = getListFragment();
			((IdspotHistoryListFragment)fragment).attachToIdspot(mIdspotId);
		}
	}

	protected IdspotHistoryListFragment getListFragment() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.idspot_history_list_fragment);
		
		if (fragment instanceof IdspotHistoryListFragment == false) {
			return null;
		}
		
		return (IdspotHistoryListFragment)fragment;
	}


	@Override
	protected BaseLocationObject dumpLocationObject(Object data) {
		if (data instanceof Cursor == false) {
			return null;
		}
		
		IdspotHistory loc = new IdspotHistory(this);
		
		loc.fillValuesFromCursor((Cursor)data);
		
		return loc;
	}
	
}
