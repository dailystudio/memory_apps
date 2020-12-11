package com.dailystudio.memory;

import java.util.List;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.game.UncommittedAchievement;
import com.dailystudio.memory.game.UncommittedAchievement.AchievementType;
import com.dailystudio.memory.game.UncommittedAchievementModal;
import com.dailystudio.memory.game.UncommittedLeaderboardScore;
import com.dailystudio.memory.game.UncommittedLeaderboardScoreModal;
import com.dailystudio.memory.loader.CheckAchievementsAndLeaderboardsLoader;
import com.dailystudio.memory.loader.LoaderController;
import com.dailystudio.memory.prefs.MemoryPrefs;
import com.dailystudio.memory.activity.AboutActivity;
import com.dailystudio.memory.activity.AbsMemoryGameListActivity;
import com.dailystudio.memory.activity.MemoryActivityListActivity;
import com.dailystudio.memory.activity.PrivacyPolicyActivity;
import com.dailystudio.memory.activity.StatisticsActivity;
import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;
import com.dailystudio.memory.database.MemoryPluginDatabaseModal;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.UncommittedAchievementLoader;
import com.dailystudio.memory.database.loader.UncommittedLeaderboardScoreLoader;
import com.dailystudio.memory.fragment.AbsMemoryListFragment;
import com.dailystudio.memory.fragment.NOWhereFragment;
import com.dailystudio.nativelib.application.AndroidActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AbsMemoryGameListActivity 
	implements OnListItemSelectedListener {
	
	private final static long SLIDER_AUTO_SHOW_DELAY = 1500;

	@SuppressWarnings("unused")
	private ImageManager mImageManager;
	
	private DrawerLayout mSlider;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        setupViews();
        
//        setSignInMessages(getString(R.string.gms_signing_in), 
//        		getString(R.string.gms_signing_out));
        
        
		LoaderController.startLoader(new CheckAchievementsAndLeaderboardsLoader(this));
    }

	protected void setupViews() {
		mImageManager = ImageManager.create(this);
		
		mSlider = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mSlider != null) {
			mSlider.setDrawerListener(new DrawerListener() {
				
				@Override
				public void onDrawerStateChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onDrawerSlide(View arg0, float arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onDrawerOpened(View arg0) {
					Fragment fragment = findFragment(R.id.fragment_nowhere);
					if (fragment instanceof NOWhereFragment) {
						((NOWhereFragment)fragment).updateNOWhere();
					}
				}
				
				@Override
				public void onDrawerClosed(View arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			if (MemoryPrefs.isShowSlideMenu(this)) {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if (mSlider != null) {
							mSlider.openDrawer(Gravity.LEFT);
							MemoryPrefs.setShowSlideMenu(
									MainActivity.this, false);
						}
					}
					
				}, SLIDER_AUTO_SHOW_DELAY);
			}
		}
		
		setupGdx();
	}
	
	private void setupGdx() {
/*		View gdxView = initializeForView(new LiveGrassGame(), true);
		
		ViewGroup gdxRoot = (ViewGroup) findViewById(R.id.gdx_root);
		if (gdxRoot != null) {
			gdxRoot.addView(gdxView);
		}
*/	}
	
	@Override
	public void onBackPressed() {
		if (mSlider != null
				&& (mSlider.isDrawerVisible(Gravity.LEFT)
						|| mSlider.isDrawerVisible(Gravity.RIGHT))) {
			mSlider.closeDrawers();
			return;
		}

		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		cancelPushUncommittedAchievementsAndLeaderboards();
	}
	
	@Override
	protected void onActionBarOverflowDisplayed() {
		super.onActionBarOverflowDisplayed();
		
		if (mSlider != null) {
			mSlider.closeDrawers();
		}
	}

	@Override
	public void onSignInSucceeded() {
		super.onSignInSucceeded();
        
        checkAndPushUncommittedAchievementsAndLeaderboards();
	};

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_main, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_search: {
				onSearchRequested();
				
				return true;
			}
		
			case R.id.menu_statistics: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), StatisticsActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
			
			case R.id.menu_privacy_policy: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), PrivacyPolicyActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
			
			case R.id.menu_about: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), AboutActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
		}
		
		return super.onOverflowItemSelected(item);
	}
	
	@Override
	public void onListItemSelected(Object data) {
		if (data instanceof Cursor) {
			launchSliderMenu((Cursor)data);
			
			return;
		} else if (data instanceof AndroidActivity) {
			((AndroidActivity)data).launch(this);
		}
	}
	
	private void launchSliderMenu(Cursor cursor) {
		AbsMemoryListFragment listFragment = getListFragment();
		if (listFragment == null) {
			return;
		}
		
		DatabaseObject object = listFragment.dumpObject(cursor);
		Logger.debug("object = %s", object);
		if (object instanceof MemoryPluginActivityCategoryObject == false) {
			return;
		}
		
		MemoryPluginActivityCategoryObject categoryObject = 
			(MemoryPluginActivityCategoryObject)object;
		
		MemoryPluginDatabaseModal.increaseActivityCategoryUsageCount(
				this, categoryObject.getPackage(), categoryObject.getCategory());
		
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), MemoryActivityListActivity.class);
		i.putExtra(Constants.EXTRA_ACTIVITY_CATEGORY, categoryObject.getCategory());
		i.putExtra(Constants.EXTRA_ACTIVITY_PACKAGE, categoryObject.getPackage());

		ActivityLauncher.launchActivity(this, i);
	}
	
	private void checkAndPushUncommittedAchievementsAndLeaderboards() {
		LoaderManager ldmgr = getSupportLoaderManager();
		if (ldmgr == null) {
			return;
		}
		
		ldmgr.initLoader(LoaderIds.MEMORY_ACHIEVEMENTS_LOADER,
				null, mAchievementLoaderCallbacks);
		ldmgr.initLoader(LoaderIds.MEMORY_LEADERBOARDS_LOADER,
				null, mLeaderboardLoaderCallbacks);
	}
	
	private void cancelPushUncommittedAchievementsAndLeaderboards() {
		LoaderManager ldmgr = getSupportLoaderManager();
		if (ldmgr == null) {
			return;
		}
		
		ldmgr.destroyLoader(LoaderIds.MEMORY_ACHIEVEMENTS_LOADER);
		ldmgr.destroyLoader(LoaderIds.MEMORY_LEADERBOARDS_LOADER);
	}

	private LoaderCallbacks<List<UncommittedAchievement>> mAchievementLoaderCallbacks = 
			new LoaderCallbacks<List<UncommittedAchievement>>() {
		
		@Override
		public void onLoaderReset(Loader<List<UncommittedAchievement>> loader) {
			Logger.debug("loader = %s", loader);
		}
		
		@Override
		public void onLoadFinished(Loader<List<UncommittedAchievement>> loader, 
				List<UncommittedAchievement> achievements) {
			Logger.debug("loader = %s, achievements = %s", 
					loader, achievements);
			if (achievements == null) {
				return;
			}
			
			final GoogleApiClient gclient = getApiClient();
			
			if (!isSignedIn()) {
				Logger.warnning("GameClient is offline = %s",
						gclient);
				
				return;
			}
			
			String achievementId = null;
			for (UncommittedAchievement achievement: achievements) {
				Logger.debug("Committing achievements = %s", 
						achievement);
				achievementId = achievement.getAchievementId();
				if (achievementId == null) {
					continue;
				}
				
				if (achievement.getAchievementType() == AchievementType.INCREMENT) {
                    Games.Achievements.increment(gclient, achievementId,
                            achievement.getIncrementSteps());
				} else {
                    Games.Achievements.unlock(gclient, achievementId);
				}
				
				UncommittedAchievementModal.removeUncommittedAchievement(
						MainActivity.this, achievement);
			} 
		}
		
		@Override
		public Loader<List<UncommittedAchievement>> onCreateLoader(int loader, Bundle args) {
			return new UncommittedAchievementLoader(MainActivity.this);
		}
		
	};

	private LoaderCallbacks<List<UncommittedLeaderboardScore>> mLeaderboardLoaderCallbacks = 
			new LoaderCallbacks<List<UncommittedLeaderboardScore>>() {
		
		@Override
		public void onLoaderReset(Loader<List<UncommittedLeaderboardScore>> loader) {
			Logger.debug("loader = %s", loader);
		}
		
		@Override
		public void onLoadFinished(Loader<List<UncommittedLeaderboardScore>> loader, 
				List<UncommittedLeaderboardScore> leaderboardScores) {
			Logger.debug("loader = %s, leaderboardScores = %s", 
					loader, leaderboardScores);
			if (leaderboardScores == null) {
				return;
			}
			
			final GoogleApiClient gclient = getApiClient();
			
			if (!isSignedIn()) {
				Logger.warnning("GameClient is offline = %s",
						gclient);
				
				return;
			}
			
			String leaderboardId = null;
			for (UncommittedLeaderboardScore leaderboardScore: leaderboardScores) {
				Logger.debug("Committing leaderboardScore = %s", 
						leaderboardScore);
				leaderboardId = leaderboardScore.getLeaderboardId();
				if (leaderboardId == null) {
					continue;
				}

                Games.Leaderboards.submitScore(gclient, leaderboardId,
						leaderboardScore.getLeaderboardScore());
				
				UncommittedLeaderboardScoreModal.removeUncommitedLeaderboardScore(
						MainActivity.this, leaderboardScore);
			} 
		}
		
		@Override
		public Loader<List<UncommittedLeaderboardScore>> onCreateLoader(int loader, Bundle args) {
			return new UncommittedLeaderboardScoreLoader(MainActivity.this);
		}
		
	};

	private Handler mHandler = new Handler();
}