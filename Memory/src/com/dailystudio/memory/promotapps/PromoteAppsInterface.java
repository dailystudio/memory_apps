package com.dailystudio.memory.promotapps;

import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.webkit.JavascriptInterface;

import com.dailystudio.app.utils.FileUtils;
import com.dailystudio.development.Logger;
import com.google.gson.Gson;

public class PromoteAppsInterface {
	
	private static final String PROMOTE_APPS_FILE = "promoteapps.json";
	
	protected Context mContext;
	
	public PromoteAppsInterface(Context context) {
		mContext = context;
	}
	
	private String getPromoteAppsJsonFromFile() {
		if (mContext == null) {
			return null;
		}
		
		String appsjson = null;
		try {
			appsjson = FileUtils.getAssetFileContent(
					mContext, PROMOTE_APPS_FILE);
		} catch (IOException e) {
			Logger.warnning("extract promote apps[%s] failure: %s",
					PROMOTE_APPS_FILE,
					e.toString());
			
			appsjson = null;
		}
		
		return appsjson;
	}

	public PromoteApps getPromoteApps() {
		String appsjson = getPromoteAppsJsonFromFile();

		return parseFromJSonString(appsjson);
	}
	
	public PromoteApps parseFromJSonString(String jsonString) {
		if (jsonString == null) {
			return null;
		}
		
		Gson gson = new Gson();
		
		PromoteApps promoteapps = null;
		try {
			promoteapps = gson.fromJson(jsonString, PromoteApps.class);
		} catch (Exception e) {
			Logger.warnning("parse promote apps failure: %s", e.toString());
			promoteapps = null;
		}
		
		if (promoteapps == null) {
			return null;
		}
		
		final PromoteApp[] appslist = promoteapps.apps;
		if (appslist == null || appslist.length <= 0) {
			return promoteapps;
		}
		
		final Resources res = mContext.getResources();
		
		int resId;
		for (PromoteApp app: appslist) {
			if (app.appName != null) {
				resId = res.getIdentifier(app.appName, "string", 
						mContext.getPackageName());
				if (resId > 0) {
					app.appName = mContext.getString(resId);
				}
			}
		}
		
		return promoteapps;
	}

	@JavascriptInterface
	public String getPromoteAppsInJson() {
		PromoteApps promoteapps = getPromoteApps();
		
		Gson gson = new Gson();
		
		return gson.toJson(promoteapps);
	}
	
}
