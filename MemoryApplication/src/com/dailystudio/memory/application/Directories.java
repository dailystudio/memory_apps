package com.dailystudio.memory.application;

import java.io.File;

import android.content.Context;

public class Directories {
	
	private static final String APP_RES_CACHES_DIR = "/appres_caches/";

	public static final String getAppResCachesDirectory(Context context) {
		final File extDir = context.getExternalFilesDir(null);
		if (extDir == null) {
			return null;
		}
		
		final File cachesDir = new File(extDir, APP_RES_CACHES_DIR);
		
		return cachesDir.getAbsolutePath();
	}

}
