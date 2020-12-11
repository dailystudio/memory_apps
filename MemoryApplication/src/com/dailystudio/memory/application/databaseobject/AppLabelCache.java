package com.dailystudio.memory.application.databaseobject;


import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class AppLabelCache extends TimeCapsule {
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package", false);
	public static final Column COLUMN_CACHED_LABEL = new TextColumn("label", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
		COLUMN_CACHED_LABEL,
	};
	
	public AppLabelCache(Context context) {
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
	
	public String getCachedLabel() {
		return getTextValue(COLUMN_CACHED_LABEL);
	}

	public void setCachedLabel(String label) {
		setValue(COLUMN_CACHED_LABEL, label);
	}
	
	@Override
	public String toString() {
		return String.format("%s, pkg = %s, label = %s",
				super.toString(),
				getPackageName(),
				getCachedLabel());
	}
}
