package com.dailystudio.memory.application;

public class Constants extends com.dailystudio.memory.Constants {
	
	public final static int APP_FILTER_FLAG_USER = 0x1;
	public final static int APP_FILTER_FLAG_SYSTEM = (0x1 << 1);
	public final static int APP_FILTER_FLAG_SDCARD = (0x1 << 2);
	
	public final static int DEFAULT_APP_FILTER_FLAGS = 
		(APP_FILTER_FLAG_USER
//				| APP_FILTER_FLAG_SYSTEM
				| APP_FILTER_FLAG_SDCARD);
	
	public final static int ALL_APP_FILTER_FLAGS = 
			(APP_FILTER_FLAG_USER
					| APP_FILTER_FLAG_SYSTEM
					| APP_FILTER_FLAG_SDCARD);

	public final static int SYSTEM_APP_ONLY_FILTER_FLAGS = 
			(APP_FILTER_FLAG_SYSTEM);
	
	public final static int USER_APP_ONLY_FILTER_FLAGS = 
			(APP_FILTER_FLAG_USER
					| APP_FILTER_FLAG_SDCARD);
	
	public final static int APP_SORT_BY_NAME = 0x1;
	public final static int APP_SORT_BY_INSTALL_TIME = 0x2;
	public final static int APP_SORT_BY_UPDATE_TIME = 0x3;
	
	public final static int DEFAULT_APP_SORT_TYPE = APP_SORT_BY_UPDATE_TIME;

	public static final String EXTRA_APP_FILTER_FLAGS = 
		"memory.intent.EXTRA_APP_FILTER_FLAGS";
	
	public static final String EXTRA_APP_PACKAGE = 
		"memory.intent.EXTRA_APP_PACKAGE";
	public static final String EXTRA_APP_LABEL = 
		"memory.intent.EXTRA_APP_LABEL";
	
	public static final String EXTRA_COMPONENTS_FILTER = 
			"memory.intent.EXTRA_COMPONENTS_FILTER";

	public static final int FAVORITE_CLASS_WEEK = 0x01;
	public static final int FAVORITE_CLASS_MONTH = 0x02;
	public static final int FAVORITE_CLASS_DAY_0_8 = 0x11;
	public static final int FAVORITE_CLASS_DAY_9_12 = 0x12;
	public static final int FAVORITE_CLASS_DAY_13_18 = 0x13;
	public static final int FAVORITE_CLASS_DAY_19_23 = 0x14;
	
	public static final int FAVORITE_APPS_LIMIT = 8;
	
	public static final String EXTRA_FAVORITE_CLASS = 
			"memory.intent.EXTRA_FAVORITE_CLASS";
	public static final String EXTRA_FAVORITE_INCLUE_SYS_APPS = 
			"memory.intent.EXTRA_FAVORITE_INCLUE_SYS_APPS";
	public static final String EXTRA_FAVORITE_APPS_TITLE = 
			"memory.intent.EXTRA_FAVORITE_APPS_TITLE";

}
