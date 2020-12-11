package com.dailystudio.memory.where.activity;

import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.hotspot.HotspotHourDistribCalculator;
import com.dailystudio.memory.where.ui.HourDistribView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class HotspotMapActivity extends BaseGeoPointMapActivity {
	
	private class CalculateHourDistribAsyncTask extends AsyncTask<String, Void, int[]> {

		@Override
		protected int[] doInBackground(String... params) {
			if (params == null || params.length < 2) {
				return null;
			}
			
			final String startTimesString = params[0];
			final String endTimesString = params[1];
			
			final HotspotHourDistribCalculator hhdCal = 
			        new HotspotHourDistribCalculator();
			
			return hhdCal.calculate(startTimesString, endTimesString);
		}
		
		@Override
		protected void onPostExecute(int[] result) {
			super.onPostExecute(result);
	        
			if (mHourDistribView != null) {
				mHourDistribView.setDistribution(result);
			}
		}
	}

	private String mStartTimes;
	private String mEndTimes;
	
	private HourDistribView mHourDistribView;
	private CalculateHourDistribAsyncTask mCalculateHourDistribAsyncTask;
	
	private View mExtrasView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hotspot_map);
		
		setupViews();
	}
	
	@Override
	protected void onPause() {
		super.onPause(); 
		
		stopCalculateHourDistrib();
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		if (intent == null) {
			return;
		}
		
		mStartTimes = intent.getStringExtra(Constants.EXTRA_START_TIMES);
		mEndTimes = intent.getStringExtra(Constants.EXTRA_END_TIMES);
		
		if (mStartTimes != null && mEndTimes != null) {
		    showExtras();
            calculateHourDistrib();
		} else {
		    hideExtras();
		}
	}

	private void setupViews() {
        mHourDistribView = (HourDistribView) findViewById(R.id.hotspot_hour_distrib);
        
        mExtrasView = findViewById(R.id.hotpot_extras);
	}
	
	private void calculateHourDistrib() {
		stopCalculateHourDistrib();
		
		mCalculateHourDistribAsyncTask = new CalculateHourDistribAsyncTask();		
		
		mCalculateHourDistribAsyncTask.executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR,
				new String[] { mStartTimes, mEndTimes });
	}
	
	private void stopCalculateHourDistrib() {
		if (mCalculateHourDistribAsyncTask != null
				&& mCalculateHourDistribAsyncTask.getStatus() == AsyncTask.Status.RUNNING
				&& mCalculateHourDistribAsyncTask.isCancelled() == false) {
			mCalculateHourDistribAsyncTask.cancel(true);
		}

		mCalculateHourDistribAsyncTask = null;
	}
	
	private void showExtras() {
        if (mExtrasView != null) {
            mExtrasView.setVisibility(View.VISIBLE);
        }
	}

    private void hideExtras() {
        if (mExtrasView != null) {
            mExtrasView.setVisibility(View.GONE);
        }
    }

}
