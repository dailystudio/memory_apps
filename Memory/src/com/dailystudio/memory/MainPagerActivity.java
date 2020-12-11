package com.dailystudio.memory;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.memory.activity.AbsMemoryListActivity;
import com.dailystudio.memory.activity.StatisticsActivity;
import com.dailystudio.memory.ui.MainPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainPagerActivity extends AbsMemoryListActivity 
	implements OnListItemSelectedListener {

	private ViewPager mPager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main_pager);
        
        setupViews();
    }

	protected void setupViews() {
		mPager = (ViewPager) findViewById(R.id.main_pager);
		if (mPager != null) {
			mPager.setAdapter(new MainPagerAdapter(this,
					getSupportFragmentManager()));
		}
			
	}

	@Override
	protected boolean onCreateOverflowMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    
		inflater.inflate(R.menu.menu_main, menu);

	    return true;
	}
	
	@Override
	protected boolean onOverflowItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_statistics: {
				Intent i = new Intent();
				
				i.setClass(getApplicationContext(), StatisticsActivity.class);
				
				ActivityLauncher.launchActivity(this, i);
				
				return true;
			}
		}
		
		return super.onOverflowItemSelected(item);
	}
	
	@Override
	public void onListItemSelected(Object data) {
	}

}