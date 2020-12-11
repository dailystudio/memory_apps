package com.dailystudio.memory.loader.step;

import java.util.List;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.plugin.PluginManager;

public class RemovePluginsStep extends BasePluginsStep {

	private String mPackageName;
	private boolean mReplacing;
	
	public RemovePluginsStep(Context context, String pkgName) {
		this(context, pkgName, false);
	}
	
	public RemovePluginsStep(Context context, String pkgName, boolean replacing) {
		super(context);
		
		mPackageName = pkgName;
		mReplacing = replacing;
	}
	
	@Override
	public boolean loadInBackground() {
		Logger.debug("mPackageName = %s, mReplacing = %s",
				mPackageName, mReplacing);
		if (mPackageName == null) {
			return false;
		}
		
		List<MemoryPluginInfo> plugins = PluginManager.listPlugins(mPackageName);
		Logger.debug("plugins: (%s)", plugins);
		if (plugins == null) {
			return true;
		}
		
		final int N = plugins.size();
		if (N <= 0) {
			return true;
		}
		
		for (int i = 0; i < N; i++) {
			PluginManager.unregisterPlugin(plugins.get(i));
		}
		
		if (mReplacing == false) {
			clearDataForPackage(mPackageName);
		}
		
		clearCacheForPackage(mPackageName);
		
		notifyPluginsChanged();
		
		return true;
	}

}
