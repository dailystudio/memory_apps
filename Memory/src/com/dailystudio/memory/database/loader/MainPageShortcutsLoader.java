package com.dailystudio.memory.database.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.database.MemoryPluginDatabaseModal;
import com.dailystudio.nativelib.application.AndroidActivity;

public class MainPageShortcutsLoader 
	extends AbsAsyncDataLoader<List<AndroidActivity>> {

	public MainPageShortcutsLoader(Context context) {
		super(context);
	}

	@Override
	public List<AndroidActivity> loadInBackground() {
		final PackageManager pm = getContext().getPackageManager();
		if (pm == null) {
			return null;
		}
		
		return findMainPageShortcuts(pm);
	}

	private List<AndroidActivity> findMainPageShortcuts(PackageManager pm) {
        Intent intent = new Intent(Constants.ACTION_MAINPAGE_SHORTCUT);
        
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 
        		PackageManager.GET_META_DATA);
        Logger.debug("activities(%s)", activities);

        final int N = (activities == null ? 
        		0 : activities.size());
        
        if (N <= 0) {
        	return null;
        }
        
        List<AndroidActivity> shortcuts = new ArrayList<AndroidActivity>();
        
        ResolveInfo rInfo = null;
        ComponentName comp = null;
        for (int i = 0; i < N; i++) {
            rInfo = activities.get(i);
            if (rInfo == null) {
            	continue;
            }
            
            comp = MemoryPluginDatabaseModal.toComponentName(rInfo);
            if (comp == null) {
            	continue;
            }
            
            shortcuts.add(new AndroidActivity(comp));
        }
        
        return shortcuts;
	}

}
