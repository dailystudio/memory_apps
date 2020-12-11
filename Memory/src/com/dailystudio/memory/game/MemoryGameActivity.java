package com.dailystudio.memory.game;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.activity.ActionBar;
import com.dailystudio.memory.game.LoginFragment.Listener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MemoryGameActivity extends BaseGameActivity implements
        GameHelper.GameHelperListener {
    
	private static final int RC_UNUSED = 5001;

	private Rect mHitRect = new Rect();

	private Object mAutoHideLock = new Object();
	private boolean mAutoHideLoginFragment = false;
	
	private ImageView mArchievementButton;
	
	private Player mPlayer;
	
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		
		setupActionBar();
		
		/*
		 * if b != null, means activity is restart by system
		 * for example, orientation is changed. In this situation,
		 * we need NOT to create a new fragment.
		 */
        LoginFragment loginFragment = null;
		if (b == null) {
			loginFragment = initLoginFragment();
		} else {
			loginFragment = findLoginFragment();
		}
		
		setupLoginFragment(loginFragment);
		hideFragmentOnCreate(loginFragment);
	};
	
	private LoginFragment initLoginFragment() {
        FragmentTransaction ft = 
        		getSupportFragmentManager().beginTransaction();
        
        final LoginFragment loginFragment = new LoginFragment();
        
        ft.add(R.id.actbar_activity_content, loginFragment);
        ft.hide(loginFragment);
        
        ft.commit();
        
        return loginFragment;
	}
	
	private void setupLoginFragment(LoginFragment loginFragment) {
		Logger.debug("loginFragment = %s", loginFragment);
		if (loginFragment == null) {
			return;
		}
		
		loginFragment.setListener(new Listener() {
			
			@Override
			public void onSignOutButtonClicked() {
		        signOut();
		        
		        LoginFragment loginFragment = 
		        		findLoginFragment();
		        if (loginFragment != null) {
		        	loginFragment.setShowSignInButton(true);
		        }
			}
			
			@Override
			public void onSignInButtonClicked() {
		        beginUserInitiatedSignIn();
		        
		        synchronized (mAutoHideLock) {
			        mAutoHideLoginFragment = true;
				}
			}

		    @Override
		    public void onShowAchievementsRequested() {
		        if (isSignedIn()) {
		            startActivityForResult(
                            Games.Achievements.getAchievementsIntent(getApiClient()),
		            		RC_UNUSED);
		        } else {
		            showAlert(getString(
		            		R.string.gms_err_achievements_not_available));
		        }
		    }

			@Override
			public void onShowLeaderBoardsRequested() {
		        if (isSignedIn()) {
		            startActivityForResult(
                            Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
		            		RC_UNUSED);
		        } else {
		            showAlert(getString(
		            		R.string.gms_err_leaderboards_not_available));
		        }
			}

        });
	}

	private void setupActionBar() {
		final ActionBar actbar = getCompatibleActionBar();
		if (actbar == null) {
			return;
		}
		
        ViewGroup v = (ViewGroup)LayoutInflater.from(this)
    		.inflate(R.layout.actbar_login, null);

	    actbar.setCustomView(v,
	        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                ActionBar.LayoutParams.WRAP_CONTENT,
	                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	    
	    mArchievementButton = (ImageView) v.findViewById(R.id.archievements_button);
	    if (mArchievementButton != null) {
	    	mArchievementButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					toggleLoginFragment();
					
			        synchronized (mAutoHideLock) {
			        	mAutoHideLoginFragment = false;
					}
				}
				
			});
	    }
	}

	protected ImageView getArchievementButton() {
		return mArchievementButton;
	}
	
	protected Player getPlayer() {
		return mPlayer;
	}
	
	@Override
	protected void signOut() {
		super.signOut();
		
		onSignOut();
	}
	
	protected void onSignOut() {
		mPlayer = null;
	}

	@Override
	public void onSignInFailed() {
        LoginFragment loginFragment = 
        		findLoginFragment();

        if (loginFragment != null) {
        	loginFragment.setShowSignInButton(true);
        }
        
        mPlayer = null;
	}

	@Override
	public void onSignInSucceeded() {
        // Set the greeting appropriately on main menu
        Player p = getPlayer();

        LoginFragment loginFragment = 
        		findLoginFragment();

        if (loginFragment != null) {
        	loginFragment.setShowSignInButton(false);

	        Logger.debug("player = %s", p);
	        
	        loginFragment.setPlayerInfo(p);
        }
        
        synchronized (mAutoHideLock) {
        	if (mAutoHideLoginFragment) {
        		mHandler.postDelayed(mHideRunnable, RUNNABLE_DELAY);
        	}

        	mAutoHideLoginFragment = false;
		}
        
        mPlayer = p;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN) {
			final int x = (int)event.getRawX();
			final int y = (int)event.getRawY();
			
			final LoginFragment loginFragment = 
					findLoginFragment();
			if (loginFragment != null 
					&& loginFragment.isVisible()) {
				
				loginFragment.getView().getGlobalVisibleRect(mHitRect);
				
				if (!mHitRect.contains(x, y)) {
					boolean hitArchievementsButton = false;
					View mOverflowButton = findViewById(R.id.archievements_button);
					if (mOverflowButton != null) {
						mOverflowButton.getGlobalVisibleRect(mHitRect);
						
						hitArchievementsButton = mHitRect.contains(x, y);
					}

					if (!hitArchievementsButton) {
						hideLoginFragment();
						
						return true;
					}
				}
			}
		}
		
		return super.dispatchTouchEvent(event);
	}
	
	private void toggleLoginFragment() {
		boolean visible =
				isFragmentVisible(R.id.actbar_activity_content);
        if (visible) {
        	hideLoginFragment();
        } else {
        	showLoginFragment();
        }
	}
	
	private void showLoginFragment() {
        showFragment(R.id.actbar_activity_content, 
        		R.anim.gsm_login_anim_in);
	}
	
	private void hideLoginFragment() {
		hideFragment(R.id.actbar_activity_content, 
        		R.anim.gsm_login_anim_out);
	}
	
	private LoginFragment findLoginFragment() {
		Fragment fragment = 
				getSupportFragmentManager().findFragmentById(
						R.id.actbar_activity_content);
		if (fragment instanceof LoginFragment == false) {
			return null; 
		}

		return (LoginFragment)fragment;
	}
	
	@SuppressWarnings("unused")
	private Runnable mShowLoginFragmentRunnable = new Runnable() {
		
		@Override
		public void run() {
			showLoginFragment();
		}

	};
	
	private Runnable mHideRunnable = new Runnable() {
		
		@Override
		public void run() {
			hideLoginFragment();
		}

	};
	
	private final static int RUNNABLE_DELAY = 1000;
	
	private Handler mHandler = new Handler();
	
}
