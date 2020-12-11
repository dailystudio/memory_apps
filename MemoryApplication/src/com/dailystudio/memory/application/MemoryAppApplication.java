package com.dailystudio.memory.application;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.dailystudio.app.DevBricksApplication;
import com.dailystudio.app.utils.FileUtils;
import com.dailystudio.development.Logger;

public class MemoryAppApplication extends DevBricksApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		
//		disableCurrentActivity();
		prepareDirs();
	}
	
	private void prepareDirs() {
		String cachesDir = Directories.getAppResCachesDirectory(this);

		FileUtils.checkOrCreateNoMediaDirectory(cachesDir);
	}
	
	protected void disableCurrentActivity() {
		final PackageManager pkgmgr = getPackageManager();
		
		final ComponentName componentName = new ComponentName(getApplicationContext(),
				AppCoreAppCheckActivity.class);
		Logger.debug("componentName = %s", componentName);
		
		pkgmgr.setComponentEnabledSetting(componentName, 
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
				PackageManager.DONT_KILL_APP);
	}


}
