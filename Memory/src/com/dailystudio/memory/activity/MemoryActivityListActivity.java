package com.dailystudio.memory.activity;

import java.util.List;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.AsyncDatabaseHelper;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;
import com.dailystudio.memory.database.MemoryPluginActivityObject;
import com.dailystudio.memory.database.MemoryPluginDatabaseModal;
import com.dailystudio.memory.fragment.AbsMemoryListFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class MemoryActivityListActivity extends AbsMemoryListActivity 
	implements OnListItemSelectedListener {

	private String mPackage;
	private String mCategory;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_activity_list);
        
        setupViews();
    }

	private void setupViews() {
	}

	@Override
	protected void bindIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		
		mPackage = intent.getStringExtra(Constants.EXTRA_ACTIVITY_PACKAGE);
		mCategory = intent.getStringExtra(Constants.EXTRA_ACTIVITY_CATEGORY);
		Logger.debug("list activities for: [cat: %s, pkg: %s]",
				mCategory, mPackage);
		updateTitleAndIcon();
	}
	
	@Override
	public void onListItemSelected(Object data) {
		if (data instanceof Cursor == false) {
			return;
		}
		
		AbsMemoryListFragment listFragment = getListFragment();
		if (listFragment == null) {
			return;
		}
		
		DatabaseObject object = listFragment.dumpObject((Cursor)data);
		Logger.debug("object = %s", object);
		if (object instanceof MemoryPluginActivityObject == false) {
			return;
		}
		
		MemoryPluginActivityObject activityObject = 
			(MemoryPluginActivityObject)object;
		
		
		final String component = activityObject.getComponent();
		if (component == null) {
			return;
		}
		
		final ComponentName androidComponent = 
			ComponentName.unflattenFromString(component);
		if (androidComponent == null) {
			return;
		}
		
		MemoryPluginDatabaseModal.increaseActivityUsageCount(
				this, androidComponent);
		
		Intent i = new Intent();
		
		/*
		 * Fix issue: 
		 *   1) Create shortcut of Boot on Launcher
		 *   2) Boot shorcut on Launcher -> Boot Chart -> Press "Home"
		 *   3) Memory -> Boot -> Boot Chart
		 *   4) Press "Back"(to Boot activity list) -> Press "Back"
		 *   
		 *   Expect: return to Memory main screen.
		 *   
		 *   Without Intent.FLAG_ACTIVITY_CLEAR_TASK, it will return to 
		 *   Launcher;
		 */
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
		i.setComponent(androidComponent);
		
		ActivityLauncher.launchActivity(this, i);
	}

	private void updateTitleAndIcon() {
		AsyncDatabaseHelper helper = 
			new AsyncDatabaseHelper(this, MemoryPluginActivityCategoryObject.class) {
			
			@Override
			protected void onQueryComplete(int token, Object cookie,
					List<DatabaseObject> objects) {
				super.onQueryComplete(token, cookie, objects);
				
				if (objects == null || objects.size() <= 0) {
					setDefaultLabelAndIcon();
					return;
				}
				
				DatabaseObject o0 = objects.get(0);
				if (o0 instanceof MemoryPluginActivityCategoryObject == false) {
					setDefaultLabelAndIcon();
					return;
				}
				
				MemoryPluginActivityCategoryObject categoryObject = 
					(MemoryPluginActivityCategoryObject)o0;
				
				String label = categoryObject.getLabel();
				CharSequence label_prefix = getString(R.string.label_activity_category);
				
				setActionBarTitle(String.format("%s: %s", 
						label_prefix, label));
			}

		};
		
		Query query = new Query(MemoryPluginActivityCategoryObject.class);
		
		ExpressionToken selToken = 
			MemoryPluginActivityCategoryObject.COLUMN_PACKAGE.eq(mPackage)
			.and(MemoryPluginActivityCategoryObject.COLUMN_CATEGORY.eq(mCategory));
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		helper.startQuery((int)System.currentTimeMillis(), null, query);
	}

	private void setDefaultLabelAndIcon() {
		ActionBar actbar = getCompatibleActionBar();
		if (actbar == null) {
			return;
		}
		
		setActionBarTitle(getString(R.string.activity_activity_list));
	}
	
	private void setActionBarTitle(String title) {
		ActionBar actbar = getCompatibleActionBar();
		if (actbar == null) {
			return;
		}
		
		actbar.setTitle(title);
	}

}