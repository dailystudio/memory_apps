package com.dailystudio.memory.activity;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.game.MemoryGameUtils;
import com.dailystudio.memory.fragment.SearchResultsListFragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MemorySearchActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_memory_search);
		
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        
        final Intent intent = getIntent();
        
        handleIntent(intent);
	}
	
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	
    	handleIntent(intent);
    }
    
    private void handleIntent(Intent intent) {
    	if (intent == null) {
    		return;
    	}
    	
        final String queryAction = intent.getAction();
        
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            doSearchQuery(intent);
        } else if (Intent.ACTION_VIEW.equals(queryAction)) {
            doView(intent);
        } else {
            Logger.debug("Create intent NOT from search");
        }
    }
    
	private void doView(Intent queryIntent) {
		// TODO Auto-generated method stub
		
	}

	private void doSearchQuery(Intent queryIntent) {
		SearchResultsListFragment fragment = 
				getResultsFragment();
		if (fragment == null) {
			return;
		}
		
		String queryInput = 
				queryIntent.getStringExtra(SearchManager.QUERY);
		String queryAuthority = 
				queryIntent.getDataString();
		Logger.debug("queryInput = %s", queryInput);
		Logger.debug("queryAuthority = %s", queryAuthority);
		
		fragment.doSearchForInput(queryInput, queryAuthority);
		
		MemoryGameUtils.unlockAchievement(getApplicationContext(), 
				getString(R.string.achievement_search_in_memory));
		MemoryGameUtils.incrementAchievement(getApplicationContext(), 
				getString(R.string.achievement_poor_memory), 1);
	}
	
	private SearchResultsListFragment getResultsFragment() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.fragment_search_results);
		
		if (fragment instanceof SearchResultsListFragment == false) {
			return null;
		}
		
		return (SearchResultsListFragment)fragment;
	}
	
}
