package com.dailystudio.memory.loader.step;

import com.dailystudio.memory.Constants;

import android.content.Context;

public class AddPluginsStep extends BasePluginsStep {

	private String mPackageName;
	
	public AddPluginsStep(Context context, String pkgName) {
		super(context);
		
		mPackageName = pkgName;
	}
	
	@Override
	public boolean loadInBackground() {
		if (mPackageName == null) {
			return false;
		}
		
		loadPlugins(Constants.CATEGORY_MAIN, mPackageName);
		
		notifyPluginsChanged();
		
		return true;
	}

}
