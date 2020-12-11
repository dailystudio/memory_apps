package com.dailystudio.memory.application.databaseobject;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

import android.content.Context;

public class UsageComponent extends TimeCapsule {
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package", false);
	public static final Column COLUMN_CLASS_NAME = new TextColumn("class", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
		COLUMN_CLASS_NAME,
	};

	public UsageComponent(Context context) {
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
	
	public String getClassName() {
		return getTextValue(COLUMN_CLASS_NAME);
	}

	public void setClassName(String activityName) {
		setValue(COLUMN_CLASS_NAME, activityName);
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): pkg(%s/%s)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getPackageName(),
				getClassName());
	}
	
}
