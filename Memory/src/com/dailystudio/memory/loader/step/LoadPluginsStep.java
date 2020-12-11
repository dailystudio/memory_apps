package com.dailystudio.memory.loader.step;

import com.dailystudio.memory.Constants;
import com.dailystudio.memory.plugin.PluginManager;

import android.content.Context;

public class LoadPluginsStep extends BasePluginsStep {

	public LoadPluginsStep(Context context) {
		super(context);
	}
	
	@Override
	public boolean loadInBackground() {
		PluginManager.clearPlugins();
		
		loadPlugins(Constants.CATEGORY_MAIN);
		
		notifyPluginsChanged();
		
		return true;
	}

}
