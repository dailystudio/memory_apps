package com.dailystudio.memory.boot.activity;

import java.util.List;

import com.dailystudio.app.dataobject.CountObject;
import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.MemoryBootCountLoader;
import com.dailystudio.memory.activity.ActionBar;
import com.dailystudio.memory.activity.ActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class BootListActivity extends ActionBarActivity {

	private TextView mCountView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_boot_list);
		
		setupActionBar();
		
		getSupportLoaderManager().initLoader(
				LoaderIds.MEMORY_BOOT_COUNT_LOADER, null, 
				mCountLoaderCallbacks);
	}
	
	private void setupActionBar() {
        ActionBar actbar = getCompatibleActionBar();
        if (actbar == null) {
        	return;
        }
        
        ViewGroup v = (ViewGroup)LayoutInflater.from(this)
    		.inflate(R.layout.boot_list_actionbar, null);

	    actbar.setCustomView(v,
	        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                ActionBar.LayoutParams.WRAP_CONTENT,
	                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	    
	    mCountView = (TextView) findViewById(R.id.boots_count);
	}

	private LoaderCallbacks<List<CountObject>> mCountLoaderCallbacks =
		new LoaderCallbacks<List<CountObject>>() {

			@Override
			public Loader<List<CountObject>> onCreateLoader(int loaderId, Bundle arg1) {
				return new MemoryBootCountLoader(BootListActivity.this);
			}

			@Override
			public void onLoadFinished(Loader<List<CountObject>> loader,
					List<CountObject> data) {
				int count = 0;

				if (data != null && data.size() > 0) {
					CountObject co = data.get(0);
					
					count = co.getCount();
				}
				
				if (mCountView != null) {
					mCountView.setText(String.valueOf(count));
				}
			}

			@Override
			public void onLoaderReset(Loader<List<CountObject>> arg0) {
			}
		
	};

	
}
