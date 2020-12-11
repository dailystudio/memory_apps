package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.ui.FloatingChildLayout;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.nativelib.application.AndroidApplication;

public class FavoriteAppsActivity extends MemoryPeroidBasedActivity
	implements OnListItemSelectedListener {

	private FloatingChildLayout mFloatingChildLayout;
	private TextView mAppsTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_favorite_apps);
		setActionBarEnabled(false);
		
		setupViews();
		
		moveCircleLayout(getIntent());
	}

	private void setupViews() {
		mFloatingChildLayout = (FloatingChildLayout) findViewById(
				R.id.fclayout);
		mFloatingChildLayout.setOnOutsideTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				
				return true;
			}
			
		});
		
		mAppsTitle = (TextView) findViewById(R.id.favorite_apps_title);
	}
	
	@Override
	protected void bindIntent(Intent intent) {
		super.bindIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		final String title = intent.getStringExtra(
				Constants.EXTRA_FAVORITE_APPS_TITLE);
		if (title != null && mAppsTitle != null) {
			mAppsTitle.setText(title);
		}
	}
	
	private void moveCircleLayout(Intent intent) {
		if (intent == null) {
			return;
		}
		
		final Rect rect = intent.getSourceBounds();
		Logger.debug("rect = %s", rect);
		if (rect == null) {
			return;
		}
		
		if (mFloatingChildLayout != null) {
			mFloatingChildLayout.setChildTargetScreen(rect);
		}
	}

	@Override
	public void onListItemSelected(Object itemData) {
		if (itemData instanceof FavoriteApp == false) {
			return;
		}
		
		FavoriteApp fApp = (FavoriteApp)itemData;
		
		final AndroidApplication app = 
				new AndroidApplication(fApp.getPackageName());
		Logger.debug("app = %s [launchIntent: %s]", 
				app, app.getLaunchIntent(this));
		app.launch(this);
		
		finish();
	}

}
