package com.dailystudio.memory.promotapps;

import android.content.Context;

import com.dailystudio.nativelib.application.AndroidApplication;

public class PromoteApp {
	
	public boolean isPublished;
	public String appName;
	public String packageName;
	public String enabledIconRes;
	public String disabledIconRes;
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): name = %s, pkg = %s, icon[enable: %s, disable: %s], published = %s",
				this.getClass().getSimpleName(),
				hashCode(),
				appName,
				packageName,
				enabledIconRes,
				disabledIconRes,
				isPublished);
	}
	
	public boolean isInstalled(Context context) {
		if (context == null || packageName == null) {
			return false;
		}
		
		AndroidApplication app = new AndroidApplication(packageName);
		
		return app.isInstalled(context);
	}
		
}
