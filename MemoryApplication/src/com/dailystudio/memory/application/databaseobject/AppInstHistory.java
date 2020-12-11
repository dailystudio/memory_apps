package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.memory.application.R;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class AppInstHistory extends CachedResMemoryObject {
	
	public static final String PACKAGE_ACTION_INSTALL = "pkg-inst";
	public static final String PACKAGE_ACTION_UNINSTALL = "pkg-uninst";
	public static final String PACKAGE_ACTION_UPDATE = "pkg-update";
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package", false);
	public static final Column COLUMN_PACKAGE_ACTION = new TextColumn("action", false);
	
	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
		COLUMN_PACKAGE_ACTION,
	};
	
	public AppInstHistory(Context context) {
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
	
	public String getPackageAction() {
		return getTextValue(COLUMN_PACKAGE_ACTION);
	}

	public void setPackageAction(String action) {
		setValue(COLUMN_PACKAGE_ACTION, action);
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): pkg(%s), action(%s)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getPackageName(),
				getPackageAction());
	}

	@Override
	protected IResourceObject createResObject() {
		final String pkgName = getPackageName();
		if (pkgName == null) {
			return null;
		}
		
		return new AndroidApplication(pkgName);
	}
	
	public static int getAciontIcon(String action) {
		if (action == null) {
			return -1;
		}
		
		int resId = -1;
		if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(action)) {
			resId = R.drawable.ic_app_inst;
		} else if (AppInstHistory.PACKAGE_ACTION_UNINSTALL.equals(action)) {
			resId = R.drawable.ic_app_uninst;
		} else if (AppInstHistory.PACKAGE_ACTION_UPDATE.equals(action)) {
			resId = R.drawable.ic_app_update;
		}
		
		return resId;
	}

	public static CharSequence getAciontLabel(Context context, String action) {
		if (context == null || action == null) {
			return null;
		}
		
		int resId = -1;
		if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(action)) {
			resId = R.string.app_inst_action_install;
		} else if (AppInstHistory.PACKAGE_ACTION_UNINSTALL.equals(action)) {
			resId = R.string.app_inst_action_remove;
		} else if (AppInstHistory.PACKAGE_ACTION_UPDATE.equals(action)) {
			resId = R.string.app_inst_action_update;
		}
		
		return context.getString(resId);
	}

	public static int getAciontSearchIcon(String action) {
		if (action == null) {
			return -1;
		}
		
		int resId = -1;
		if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(action)) {
			resId = R.drawable.ic_app_search_inst;
		} else if (AppInstHistory.PACKAGE_ACTION_UNINSTALL.equals(action)) {
			resId = R.drawable.ic_app_search_uninst;
		} else if (AppInstHistory.PACKAGE_ACTION_UPDATE.equals(action)) {
			resId = R.drawable.ic_app_search_update;
		}
		
		return resId;
	}

	@Override
	protected String getResourcePackage() {
		return getPackageName();
	}

}
