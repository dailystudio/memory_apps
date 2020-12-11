package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class FavoriteApp extends CachedResMemoryObject {
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package", false);
	public static final Column COLUMN_FAVORTIE_CLASS = new IntegerColumn("favorite_class", false);
	public static final Column COLUMN_SYSTEM_APP = new IntegerColumn("sysapp", false);
	public static final Column COLUMN_LOCKED = new IntegerColumn("locked", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
		COLUMN_FAVORTIE_CLASS,
		COLUMN_SYSTEM_APP,
		COLUMN_LOCKED,
	};
	
	private boolean mHiResIconRequired = false;

	public FavoriteApp(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public String getPackageName() {
		return getTextValue(COLUMN_PACKAGE_NAME);
	}

	public void setPackageName(String pkgName) {
		setValue(COLUMN_PACKAGE_NAME, pkgName);
	}
	
	public int getFavoriteClass() {
		return getIntegerValue(COLUMN_FAVORTIE_CLASS);
	}

	public void setFavoriteClass(int favouriteClass) {
		setValue(COLUMN_FAVORTIE_CLASS, favouriteClass);
	}
	
	public boolean isSystemApp() {
		return (getIntegerValue(COLUMN_SYSTEM_APP) == 1);
	}

	public void setSystemApp(boolean isSysApp) {
		setValue(COLUMN_SYSTEM_APP, (isSysApp ? 1 : 0));
	}
	
	public boolean isLocked() {
		return (getIntegerValue(COLUMN_LOCKED) == 1);
	}

	public void setLocked(boolean locked) {
		setValue(COLUMN_LOCKED, (locked ? 1 : 0));
	}
	
	public void setHiResIconRequired(boolean required) {
		mHiResIconRequired = required;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): favourite = %d, pkg = %s [sys: %s, locked: %s]",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getFavoriteClass(),
				getPackageName(),
				isSystemApp(),
				isLocked());
	}

	@Override
	protected String getResourcePackage() {
		return getPackageName();
	}

	@Override
	protected IResourceObject createResObject() {
		AndroidApplication app =
				new AndroidApplication(getPackageName());
		
		if (mHiResIconRequired) {
			app.setHiResIconRequired(true);
		}
		
		return app;
	}
	
	public static String favoriteClassToString(int favoriteClass) {
		StringBuilder builder = new StringBuilder();
		
		switch (favoriteClass) {
			case Constants.FAVORITE_CLASS_MONTH:
				builder.append("Month");
				break;
	
			case Constants.FAVORITE_CLASS_WEEK:
				builder.append("Week");
				break;
	
			case Constants.FAVORITE_CLASS_DAY_0_8:
				builder.append("Day[0 - 8]");
				break;
	
			case Constants.FAVORITE_CLASS_DAY_9_12:
				builder.append("Day[9 - 12]");
				break;
	
			case Constants.FAVORITE_CLASS_DAY_13_18:
				builder.append("Day[13 - 18]");
				break;
	
			case Constants.FAVORITE_CLASS_DAY_19_23:
				builder.append("Day[19 - 23]");
				break;
	
			default:
				builder.append("n/A");
				break;
		}
		
		return builder.toString();
	}
	
	public static int timeToFavoriteClass(long time) {
		final int hour = CalendarUtils.getHour(time);
		
		int fclass = Constants.FAVORITE_CLASS_WEEK;
		switch (hour) {
			case 0: case 1: case 2:
			case 3: case 4: case 5:
			case 6: case 7: case 8:
				fclass = Constants.FAVORITE_CLASS_DAY_0_8;
				break;
				
			case 9: case 10: 
			case 11: case 12:
				fclass = Constants.FAVORITE_CLASS_DAY_9_12;
				break;
				
			case 13: case 14: case 15:
			case 16: case 17: case 18:
				fclass = Constants.FAVORITE_CLASS_DAY_13_18;
				break;
				
			case 19: case 20: case 21:
			case 22: case 23:
				fclass = Constants.FAVORITE_CLASS_DAY_19_23;
				break;
		}
		
		return fclass;
	}

	public static String favoriteClassToLabel(Context context, int favoriteClass) {
		if (context == null) {
			return favoriteClassToString(favoriteClass);
		}
		
		int resId = R.string.favorite_class_week;
		switch (favoriteClass) {
			case Constants.FAVORITE_CLASS_MONTH:
				resId = R.string.favorite_class_month;
				break;
	
			case Constants.FAVORITE_CLASS_WEEK:
				resId = R.string.favorite_class_week;
				break;
	
			case Constants.FAVORITE_CLASS_DAY_0_8:
				resId = R.string.favorite_class_day_0_8;
				break;
	
			case Constants.FAVORITE_CLASS_DAY_9_12:
				resId = R.string.favorite_class_day_9_12;
				break;
	
			case Constants.FAVORITE_CLASS_DAY_13_18:
				resId = R.string.favorite_class_day_13_18;
				break;
	
			case Constants.FAVORITE_CLASS_DAY_19_23:
				resId = R.string.favorite_class_day_19_23;
				break;
	
			default:
				resId = R.string.favorite_class_week;
				break;
		}
		
		return context.getString(resId);
	}


}
