package com.dailystudio.memory.application.activity;

import android.content.Intent;
import android.os.Bundle;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.memory.activity.MemoryPeroidBasedActivity;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.FavoriteAppsSetting;
import com.dailystudio.memory.application.R;

public class CreateFavoriteAppsShortcutActivity extends MemoryPeroidBasedActivity 
	implements OnListItemSelectedListener {


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_create_favorite_apps_shortcut);
        
        setupViews();
    }

	protected void setupViews() {
	}

	@Override
	public void onListItemSelected(Object data) {
		if (data instanceof FavoriteAppsSetting == false) {
			return;
		}
        
		FavoriteAppsSetting setting = (FavoriteAppsSetting)data;
		
		Intent shortcutIntent = new Intent();
		
		shortcutIntent.setClass(getApplicationContext(), FavoriteAppsActivity.class);
        shortcutIntent.putExtra(Constants.EXTRA_FAVORITE_CLASS, setting.favortieClass);
        shortcutIntent.putExtra(Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS, setting.includeSystemApps);
        shortcutIntent.putExtra(Constants.EXTRA_FAVORITE_APPS_TITLE, getString(setting.displayLabelResId));
		
        Intent intent = new Intent(Intent.ACTION_MAIN);
		
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, 
                		setting.iconResId));
        
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, 
        		getString(setting.displayLabelResId));
        
        setResult(RESULT_OK, intent);
        
        finish();
	}

}
