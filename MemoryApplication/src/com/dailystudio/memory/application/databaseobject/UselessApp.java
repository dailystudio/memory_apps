package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.dataobject.TimeColumn;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class UselessApp extends CachedResMemoryObject {
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package", false);
	public static final Column COLUMN_NOT_USED_RECENTLY = new IntegerColumn("not_used_recently", false);
	public static final Column COLUMN_NOT_UPDATED_RECENTLY = new IntegerColumn("not_updated_recently", false);
	public static final Column COLUMN_RECENT_USED_TIME = new TimeColumn("recent_used_time", false);
	public static final Column COLUMN_RECENT_UPDATED_TIME = new TimeColumn("recent_updated_time", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
		COLUMN_NOT_USED_RECENTLY,
		COLUMN_NOT_UPDATED_RECENTLY,
		COLUMN_RECENT_USED_TIME,
		COLUMN_RECENT_UPDATED_TIME,
	};
	
	public UselessApp(Context context) {
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
	
	public boolean isNotUsedRecently() {
		return (getIntegerValue(COLUMN_NOT_USED_RECENTLY) == 1);
	}
	
	public void setNotUsedRecently(boolean notUsedRecently) {
		setValue(COLUMN_NOT_USED_RECENTLY, (notUsedRecently ? 1 : 0));
	}

	public boolean isNotUpdatedRecently() {
		return (getIntegerValue(COLUMN_NOT_UPDATED_RECENTLY) == 1);
	}
	
	public void setNotUpdatedRecently(boolean notUpdatedRecently) {
		setValue(COLUMN_NOT_UPDATED_RECENTLY, (notUpdatedRecently ? 1 : 0));
	}
	
	public long getRecentUsedTime() {
		return getLongValue(COLUMN_RECENT_USED_TIME);
	}

	public void setRecentUsedTime(long time) {
		setValue(COLUMN_RECENT_USED_TIME, time);
	}

	public long getRecentUpdatedTime() {
		return getLongValue(COLUMN_RECENT_UPDATED_TIME);
	}

	public void setRecentUpdatedTime(long time) {
		setValue(COLUMN_RECENT_UPDATED_TIME, time);
	}
	
	public Context getContext() {
		return mContext;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): pkg(%s), notUsedRecently(%s), notUpdated(%s), recent[used: %s, updated:%s]",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getPackageName(),
				isNotUsedRecently(), 
				isNotUpdatedRecently(),
				CalendarUtils.timeToReadableString(getRecentUsedTime())	,
				CalendarUtils.timeToReadableString(getRecentUpdatedTime()));
	}

	@Override
	protected IResourceObject createResObject() {
		final String pkgName = getPackageName();
		if (pkgName == null) {
			return null;
		}
		
		return new AndroidApplication(pkgName);
	}
	
	public AndroidApplication getApplication() {
		final IResourceObject resObject = getResObject();
		if (resObject instanceof AndroidApplication == false) {
			return null;
		}

		return (AndroidApplication)resObject;
	}

	@Override
	protected String getResourcePackage() {
		return getPackageName();
	}

}
