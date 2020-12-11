package com.dailystudio.memory.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;
import com.dailystudio.memory.fragment.AbsMemoryListFragment;

public class CreateCategoryShortcutActivity extends AbsMemoryListActivity 
	implements OnListItemSelectedListener {


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_create_category_shortcut);
        
        setupViews();
    }

	protected void setupViews() {
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
		if (object instanceof MemoryPluginActivityCategoryObject == false) {
			return;
		}
		
		MemoryPluginActivityCategoryObject categoryObject = 
			(MemoryPluginActivityCategoryObject)object;
		
		final String categoryPackage = categoryObject.getPackage();
		if (categoryPackage == null) {
			return;
		}
			
		Intent shortcutIntent = new Intent();
		
		shortcutIntent.setClass(getApplicationContext(), MemoryActivityListActivity.class);
		shortcutIntent.putExtra(Constants.EXTRA_ACTIVITY_CATEGORY, categoryObject.getCategory());
		shortcutIntent.putExtra(Constants.EXTRA_ACTIVITY_PACKAGE, categoryObject.getPackage());
        
		Context categoryContext = null;
		
		try {
			categoryContext = createPackageContext(
					categoryObject.getPackage(), CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			Logger.warnning("create context for pkg[%s] failure: %s", 
					categoryPackage, e.toString());
			categoryContext = null;
		} catch (SecurityException e) {
			Logger.warnning("create context for pkg[%s] failure: %s", 
					categoryPackage, e.toString());
			categoryContext = null;
		}
		
		if (categoryContext == null) {
			return;
		}
			
		Intent intent = new Intent(Intent.ACTION_MAIN);
		
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(categoryContext, 
                		categoryObject.getIcon()));
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, categoryObject.getLabel());
        
        setResult(RESULT_OK, intent);
        
        finish();
	}

}
