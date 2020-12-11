package com.dailystudio.memory.where.activity;

import java.text.SimpleDateFormat;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.CameraPosition;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class IdentifiedHotspotMapActivity extends BaseGeoPointMapActivity {
	
	private final static float CAMERA_IN_360_MOVE_STEP = 90.0f;
	private final static float CAMERA_IN_360_DEFAULT_BEARING = 90.0f;
	private final static int CAMERA_IN_360_MOVE_DURATION = 6000;
	private final static int CAMERA_IN_360_MOVE_DELAY = 500;
	private final static int CAMERA_IN_360_START_DELAY = 2500;
	
	private int mQuestionId;
	
	private TextView mExtraStatsToday;
	private TextView mExtraStatsThisWeek;
	private TextView mExtraStatsThisMonth;
	
	private int mIdspotId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_idspot_map);
		
		setupViews();
		
		requestMoveCameraIn360(CAMERA_IN_360_START_DELAY);
	}
	
	private void setupViews() {
		mExtraStatsToday = (TextView) findViewById(R.id.idspot_stat_today);
		if (mExtraStatsToday != null) {
			mExtraStatsToday.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					
					i.setClass(getApplicationContext(), 
							IdspotDayStatChartActivity.class);
					
					final long now = System.currentTimeMillis();
					
					final long dayStart = CalendarUtils.getStartOfDay(now); 
					final long dayEnd = CalendarUtils.getEndOfDay(now);
					
					i.putExtra(Constants.EXTRA_PEROID_START, dayStart);
					i.putExtra(Constants.EXTRA_PEROID_END, dayEnd);
					
					ActivityLauncher.launchActivity(
							IdentifiedHotspotMapActivity.this, i);
				}
				
			});
		}

		mExtraStatsThisWeek = (TextView) findViewById(R.id.idspot_stat_this_week);
		if (mExtraStatsThisWeek != null) {
			mExtraStatsThisWeek.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					
					i.setClass(getApplicationContext(), 
							IdspotWeekStatChartActivity.class);
					
					final long now = System.currentTimeMillis();
					
					final long dayStart = CalendarUtils.getStartOfWeek(now); 
					final long dayEnd = CalendarUtils.getEndOfWeek(now);
					
					i.putExtra(Constants.EXTRA_PEROID_START, dayStart);
					i.putExtra(Constants.EXTRA_PEROID_END, dayEnd);
					i.putExtra(Constants.EXTRA_IDSPOT_ID, mIdspotId);
					
					ActivityLauncher.launchActivity(
							IdentifiedHotspotMapActivity.this, i);
				}
				
			});
		}

		mExtraStatsThisMonth = (TextView) findViewById(R.id.idspot_stat_this_month);
		if (mExtraStatsThisMonth != null) {
			mExtraStatsThisMonth.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
				
			});
		}

	}
	
	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_overflow_idspot, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_idspot_history: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), IdspotHistoryListActivity.class);
				
				i.putExtra(Constants.EXTRA_IDSPOT_ID, mIdspotId);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
			
		}
		
		return super.onOverflowItemSelected(item);
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		if (intent == null) {
			return;
		}
		
		mQuestionId = intent.getIntExtra(Constants.EXTRA_QUESTION_ID, 
				Constants.INVALID_ID);
		
		mIdspotId = intent.getIntExtra(Constants.EXTRA_IDSPOT_ID,
				Constants.INVALID_ID);

		refreshStatistics();
	}
	
	private void refreshStatistics() {
		final long now = System.currentTimeMillis();
		
		SimpleDateFormat sdf = null;
		
		if (mExtraStatsToday != null) {
			sdf = new SimpleDateFormat(getString(
					R.string.idspot_stat_label_day_templ));
			
			mExtraStatsToday.setText(sdf.format(now));
		}
		
		if (mExtraStatsThisWeek != null) {
			mExtraStatsThisWeek.setText(String.format(getString(
					R.string.idspot_stat_label_week_templ), 
					CalendarUtils.getWeek(now)));
		}
		
		if (mExtraStatsThisMonth != null) {
			sdf = new SimpleDateFormat(getString(
					R.string.idspot_stat_label_month_templ));
			
			mExtraStatsThisMonth.setText(sdf.format(now));
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
	
	private void requestMoveCameraIn360(long delay) {
		mHandler.postDelayed(m360CameraRunnable, delay);
	}
	
	private void moveCameraIn360() {
		final GoogleMap map = getMap();
		if (map == null) {
			return;
		}
		
		float bearing = CAMERA_IN_360_DEFAULT_BEARING;
		
		CameraPosition cameraPosition = map.getCameraPosition();
		if (cameraPosition != null) {
			bearing = cameraPosition.bearing;
		}
		
		CameraPosition newPos = new CameraPosition.Builder(cameraPosition)
			.bearing(bearing + CAMERA_IN_360_MOVE_STEP)
			.build();
		
		map.animateCamera(CameraUpdateFactory.newCameraPosition(newPos), 
				CAMERA_IN_360_MOVE_DURATION,
				m360CameraUpdateCallback);
	}
	

	private CancelableCallback m360CameraUpdateCallback = new CancelableCallback() {
		
		@Override
		public void onFinish() {
			requestMoveCameraIn360(CAMERA_IN_360_MOVE_DELAY);
		}
		
		@Override
		public void onCancel() {
			requestMoveCameraIn360(CAMERA_IN_360_MOVE_DELAY);
		}
		
	};

	private Runnable m360CameraRunnable = new Runnable() {
		
		@Override
		public void run() {
			moveCameraIn360();
		}
		
	};
	
	private Handler mHandler = new Handler();

}