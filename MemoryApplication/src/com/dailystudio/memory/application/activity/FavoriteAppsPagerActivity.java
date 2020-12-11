package com.dailystudio.memory.application.activity;


import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.application.R;

import android.os.Bundle;

public class FavoriteAppsPagerActivity extends ActionBarActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_favorite_apps_pager);

        setupViews();
    }

	
	private void setupViews() {
	}
	
}