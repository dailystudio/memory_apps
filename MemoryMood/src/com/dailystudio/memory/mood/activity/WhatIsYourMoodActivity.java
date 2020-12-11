package com.dailystudio.memory.mood.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.game.MemoryGameUtils;
import com.dailystudio.memory.activity.ActionBar;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.mood.Constants;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.mood.fragment.NewMoodConfirmDialogFragment;
import com.dailystudio.memory.mood.fragment.WhatIsYourMoodFragment;
import com.dailystudio.memory.ui.AlertDialogFragment;
import com.dailystudio.memory.ui.AlertDialogFragment.AlertDialogFragmentCallbacks;

public class WhatIsYourMoodActivity extends ActionBarActivity 
	implements OnListItemSelectedListener {

	private ImageView mMoodDisplayModeView = null;
	
	private int mSelectedMoodIdentifier = -1;
	
	private int mQuestionId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_what_is_your_mood);
		
		parseAskInfo(getIntent());
		
		setupActionBar();
	}

	private void setupActionBar() {
        ActionBar actbar = getCompatibleActionBar();
        if (actbar == null) {
        	return;
        }
        
        ViewGroup v = (ViewGroup)LayoutInflater.from(this)
    		.inflate(R.layout.what_is_your_mood_actbar, null);

	    actbar.setCustomView(v,
	        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                ActionBar.LayoutParams.WRAP_CONTENT,
	                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	    
	    mMoodDisplayModeView = (ImageView)findViewById(R.id.act_bar_mood_display_mode);
	    if (mMoodDisplayModeView != null) {
	    	mMoodDisplayModeView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					WhatIsYourMoodFragment wiym = 
						getMainFragment();
					
					wiym.toogleMoodDisplayMode();
					
					updateMoodDisplayModeIcon();
				}
				
			});
	    	
	    	updateMoodDisplayModeIcon();
	    }
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		parseAskInfo(intent);
	}
	
	private void parseAskInfo(Intent intent) {
		if (intent == null) {
			return;
		}
		
		mQuestionId = intent.getIntExtra(Constants.EXTRA_QUESTION_ID, 
				Constants.INVALID_ID);
	}
	
	private WhatIsYourMoodFragment getMainFragment() {
		Fragment fragment = 
			getSupportFragmentManager().findFragmentById(R.id.fragment_wiym);
		if (fragment instanceof WhatIsYourMoodFragment == false) {
			return null;
		}
		
		return (WhatIsYourMoodFragment)fragment;
	}
	
	private void updateMoodDisplayModeIcon() {
		WhatIsYourMoodFragment wiym = 
			getMainFragment();
		
		final boolean lvlDisplayed = wiym.isMoodLevelDisplayed();
		
		if (mMoodDisplayModeView != null) {
			mMoodDisplayModeView.setImageResource(lvlDisplayed ?
					R.drawable.ic_actbar_mood_lvl_hide : 
						R.drawable.ic_actbar_mood_lvl_show);
		}
	}

	@Override
	public void onListItemSelected(Object itemData) {
		if (itemData instanceof Mood == false) {
			return;
		}
		
		Mood mood = (Mood)itemData;
		
		mSelectedMoodIdentifier = mood.identifier;
		
		AlertDialogFragment newMoodConfirmDialog = 
			new NewMoodConfirmDialogFragment();
		
		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_MOOD_ID, mSelectedMoodIdentifier);
		
		newMoodConfirmDialog.setArguments(args);
		newMoodConfirmDialog.setCallbacks(new AlertDialogFragmentCallbacks() {
			
			@Override
			public void onDialogConfirmed(AlertDialogFragment fragment, 
					DialogInterface dialog, int which) {
				if (fragment instanceof NewMoodConfirmDialogFragment == false) {
					return;
				}
				
				Logger.debug("mSelectedMoodIdentifier = %d", mSelectedMoodIdentifier);
				
				if (mSelectedMoodIdentifier == -1) {
					return;
				}
				
				String comments = 
					((NewMoodConfirmDialogFragment)fragment).getComments();
				
				MemoryMood mm = new MemoryMood(WhatIsYourMoodActivity.this);
					
				mm.setTime(System.currentTimeMillis());
				mm.setMood(mSelectedMoodIdentifier);
				
				if (comments != null) {
					mm.setComment(comments);
				}
				
				Logger.debug("mm = %s", mm);
				TimeCapsuleDatabaseWriter<MemoryMood> writer = 
					new TimeCapsuleDatabaseWriter<MemoryMood>(WhatIsYourMoodActivity.this,
							MemoryMood.class);
				
				writer.insert(mm);
				
				if (mm.getMoodLevel() >= 5) {
					unlockShinyDayAchievement();
				} else if (mm.getMoodLevel() <= -5) {
					unlockPoorGuyAchievement();
				}
				
				if (mQuestionId != Constants.INVALID_ID) {
					MemoryAsk.answerQuestion(WhatIsYourMoodActivity.this, 
							mQuestionId, String.valueOf(mSelectedMoodIdentifier));
				}
				
				final boolean launchedByWidget = isLaunchedByAppWidget();
				Logger.debug("launchedByWidget = %s", launchedByWidget);
				if (!launchedByWidget) {
					launchDayChartActivity();
				}
				
				finish();
			}
			
			@Override
			public void onDialogCancelled(AlertDialogFragment fragment, 
					DialogInterface dialog, int which) {
				mSelectedMoodIdentifier = -1;
			}
			
		});
		
		newMoodConfirmDialog.show(
				getSupportFragmentManager(), "new-mood-confirm");
	}
	
	private void unlockShinyDayAchievement() {
		MemoryGameUtils.unlockAchievement(this, 
				getString(R.string.achievement_shinny_day));
	}
	
	private void unlockPoorGuyAchievement() {
		MemoryGameUtils.unlockAchievement(this, 
				getString(R.string.achievement_poor_guy));
	}

	private void launchDayChartActivity() {
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), MoodByDayChartActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		ActivityLauncher.launchActivity(this, i);
	}
	
	private boolean isLaunchedByAppWidget() {
		Intent i = getIntent();
		if (i == null) {
			return false;
		}
		
		return i.getBooleanExtra(Constants.EXTRA_LAUNCHED_BY_APP_WIDGET, false);
	}
	
}
