package com.dailystudio.memory.activity;

import com.dailystudio.memory.R;
import com.dailystudio.memory.fragment.ShowcaseFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class ShowcaseActivity extends ActionBarActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_showcase);

        setupViews();
    }

	
	private void setupViews() {
	}
	
	protected ShowcaseFragment getShowcaseFragment() {
		FragmentManager frgmgr = getSupportFragmentManager();
		if (frgmgr == null) {
			return null;
		}
		
		Fragment fragment = frgmgr.findFragmentById(
				R.id.showcase_fragment);
		if (fragment instanceof ShowcaseFragment == false) {
			return null;
		}
		
		return (ShowcaseFragment)fragment;
	}

}