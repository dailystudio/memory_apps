package com.dailystudio.memory.activity;

import java.util.HashSet;
import java.util.Set;

import com.dailystudio.app.ui.AnimatedImageView;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.fragment.AbsStatisticsFragment.StatisticCallbacks;
import com.dailystudio.memory.fragment.StatisticsLifeTimeFragment;
import com.dailystudio.memory.fragment.StatisticsPiecesFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class StatisticsActivity extends ActionBarActivity {
	
	private AnimatedImageView mStatsIcon;

	private Animation mStatsIconInAnim;
	private Animation mStatsIconOutAnim;
	
	private Set<String> mRunningStats = new HashSet<String>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_statistics);

        setupActionBar();
        
        setupViews();
    }

	private void setupActionBar() {
        ActionBar actbar = getCompatibleActionBar();
        if (actbar == null) {
        	return;
        }
        
        ViewGroup v = (ViewGroup)LayoutInflater.from(this)
    		.inflate(R.layout.statistics_actbar, null);

	    actbar.setCustomView(v,
	        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                ActionBar.LayoutParams.WRAP_CONTENT,
	                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	    
	    mStatsIcon = (AnimatedImageView)findViewById(R.id.act_bar_stats_icon);
	    if (mStatsIcon != null) {
	    	mStatsIconInAnim = AnimationUtils.loadAnimation(this, R.anim.stats_icon_in);
	    	mStatsIconOutAnim = AnimationUtils.loadAnimation(this, R.anim.stats_icon_out);
	    	if (mStatsIconOutAnim != null) {
	    		mStatsIconOutAnim.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						if (mStatsIcon != null) {
							mStatsIcon.setVisibility(View.GONE);
						}
					}
					
				});
	    	}
	    }
	}
	
	private void setupViews() {
		FragmentManager frgmgr = getSupportFragmentManager();
		if (frgmgr == null) {
			return;
		}
		
		StatisticsLifeTimeFragment ltFragment =
			(StatisticsLifeTimeFragment) frgmgr.findFragmentById(R.id.stats_lifetime_fragment);
		if (ltFragment != null) {
			ltFragment.setStatisticCallbacks(mStatisticsCallbacks);
		}
		
		StatisticsPiecesFragment picsFragment =
			(StatisticsPiecesFragment) frgmgr.findFragmentById(R.id.stats_pieces_fragment);
		if (picsFragment != null) {
			picsFragment.setStatisticCallbacks(mStatisticsCallbacks);
		}
	}

	private StatisticCallbacks mStatisticsCallbacks = new StatisticCallbacks() {

		@Override
		public void onStatisticBegin(String token) {
			Logger.debug("token = %s", token);
			if (token == null) {
				return;
			}
			
			mRunningStats.add(token);
			Logger.debug("mRunningStats = %s", mRunningStats);
			
			if (mRunningStats.size() > 0) {
				startPromptStatistics();
			}
		}

		@Override
		public void onStatisticEnd(String token) {
			Logger.debug("token = %s", token);
			if (token == null) {
				return;
			}
			
			mRunningStats.remove(token);
			Logger.debug("mRunningStats = %s", mRunningStats);
			
			if (mRunningStats.size() <= 0) {
				stopPromptStatistics();
			}
		}
		
		private void startPromptStatistics() {
			if (mStatsIcon != null) {
				mStatsIcon.setVisibility(View.VISIBLE);
				
				if (mStatsIconInAnim != null) {
					mStatsIcon.startAnimation(mStatsIconInAnim);
				}
			}
		}

		private void stopPromptStatistics() {
			if (mStatsIcon != null) {
				mStatsIcon.stopAnimation();
				if (mStatsIconOutAnim != null) {
					mStatsIcon.startAnimation(mStatsIconOutAnim);
				}
			}
		}

	};

}