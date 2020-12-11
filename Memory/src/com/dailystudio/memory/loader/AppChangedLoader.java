package com.dailystudio.memory.loader;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.loader.step.AddPluginsStep;
import com.dailystudio.memory.loader.step.CheckAndPromoteMemoryAppStep;
import com.dailystudio.memory.loader.step.RemovePluginsStep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppChangedLoader extends MemoryStepLoader {

	public AppChangedLoader(Context context, Intent changedIntent) {
		super(context);
		
		initMembers();
		
		initSteps(changedIntent);
	}
	
	private void initMembers() {
	}

	private void initSteps(Intent i) {
		if (i == null) {
			return;
		}
		
		final String action = i.getAction();
		Logger.debug("action: %s", action);
		if (action == null) {
			return;
		}
		
		final Uri data = i.getData();
		if (data == null) {
			return;
		}
		
		final String pkgName = data.getSchemeSpecificPart();
		Logger.debug("pkgName: %s", pkgName);

		final boolean replacing = i.getBooleanExtra(Intent.EXTRA_REPLACING, false);
		
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			Logger.debug("PKG_ADD: %s (replacing: %s)", 
					pkgName,
					replacing);
			if (replacing) {
				addStep(new RemovePluginsStep(mContext, pkgName, true));
			}
			addStep(new AddPluginsStep(mContext, pkgName));
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			Logger.debug("PKG_REMOVED: %s (replacing: %s)", 
					pkgName,
					replacing);
			addStep(new RemovePluginsStep(mContext, pkgName));
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			Logger.debug("PKG_REPLACED: %s", pkgName);
		} else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
			Logger.debug("PKG_CHANGED: %s", pkgName);
		}

		addStep(new CheckAndPromoteMemoryAppStep(mContext));

		printSteps();
	}

}
