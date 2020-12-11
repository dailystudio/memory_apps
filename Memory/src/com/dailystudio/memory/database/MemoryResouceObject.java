package com.dailystudio.memory.database;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryResouceObject extends TimeCapsule {

	public static final Column COLUMN_LABEL = new TextColumn("label", false);
	public static final Column COLUMN_ICON = new IntegerColumn("icon");
	
	public static final Column COLUMN_PACKAGE = new TextColumn("package", false);
	
	private final static Column[] sCloumns = {
		COLUMN_LABEL,
		COLUMN_ICON,
		COLUMN_PACKAGE,
	};

	public MemoryResouceObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public String getLabel() {
		return getTextValue(COLUMN_LABEL);
	}

	public void setLabel(String label) {
		setValue(COLUMN_LABEL, label);
	}
	
	public int getIcon() {
		return getIntegerValue(COLUMN_ICON);
	}

	public void setIcon(int iconResId) {
		setValue(COLUMN_ICON, iconResId);
	}
	
	public String getIconIdentifier() {
		final String pkg = getPackage();
		final int resId = getIcon();
		
		if (pkg == null || resId < 0) {
			return null;
		}
		
		StringBuilder b = new StringBuilder(pkg);
		
		b.append(":");
		b.append(resId);
		
		return b.toString();
	}
	
	public String getPackage() {
		return getTextValue(COLUMN_PACKAGE);
	}
	
	public void setPakcage(String pkgName) {
		setValue(COLUMN_PACKAGE, pkgName);
	}

	@Override
	public String toString() {
		return String.format("%s, package = %s, label = %s, iconResId = %d",
				super.toString(),
				getPackage(),
				getLabel(),
				getIcon());
	}

}
