package com.dailystudio.memory.application.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.nativelib.application.AndroidApplication;

public class AndroidApplicationsLoader extends AbsAsyncDataLoader<List<AndroidApplication>> {

	private int mFilterFlags = 0;
	
	public AndroidApplicationsLoader(Context context, int filterFlags) {
		super(context);
		
		mFilterFlags = filterFlags;
	}

	@Override
	public List<AndroidApplication> loadInBackground() {
		List<AndroidApplication> allApps =
			AndroidApplication.queryApplications(getContext());
		
		List<AndroidApplication> filteredApps = 
			new ArrayList<AndroidApplication>();
		
		final boolean appSys = 
			((mFilterFlags & Constants.APP_FILTER_FLAG_SYSTEM) == Constants.APP_FILTER_FLAG_SYSTEM);
		final boolean appSdCard = 
			((mFilterFlags & Constants.APP_FILTER_FLAG_SDCARD) == Constants.APP_FILTER_FLAG_SDCARD);
		final boolean appNormal =
			((mFilterFlags & Constants.APP_FILTER_FLAG_USER) == Constants.APP_FILTER_FLAG_USER);
		
		for (AndroidApplication app: allApps) {
			if (!app.isSystem() && !app.isOnSdCard()
					&& !appNormal) {
				continue;
			} else if (!appSys && app.isSystem()) {
				continue;
			} else if (!appSdCard && app.isOnSdCard()) {
				continue;
			} 
				
			filteredApps.add(app);
		}
		
		return filteredApps;
	}

}
