package com.dailystudio.memory.prefs;

import android.content.Context;

import com.dailystudio.app.prefs.AbsPrefs;

public class MemoryPrefs extends AbsPrefs {

	private final static String MEMORY_PREFS = "memory_prefs";

	private final static String PREF_SHOW_SLIDE_MENU = "show_slide_menu";
	
	private final static MemoryPrefs sPref = new MemoryPrefs();
	
	@Override
	protected String getPrefName() {
		return MEMORY_PREFS;
	}
	
	public synchronized static boolean isShowSlideMenu(Context context) {
		return sPref.getBooleanPrefValue(context, PREF_SHOW_SLIDE_MENU, true);
	}
	
	public synchronized static void setShowSlideMenu(Context context, boolean show) {
		sPref.setBooleanPrefValue(context, PREF_SHOW_SLIDE_MENU, show);
	}
	
}
