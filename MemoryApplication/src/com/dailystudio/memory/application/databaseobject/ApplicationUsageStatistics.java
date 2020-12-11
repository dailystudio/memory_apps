package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class ApplicationUsageStatistics extends UsageStatistics {
	
	public static final Column COLUMN_PACKAGE_NAME = new TextColumn("package_name", false);

	private final static Column[] sCloumns = {
		COLUMN_PACKAGE_NAME,
	};
	
	public ApplicationUsageStatistics(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public void setPackageName(String pkgName) {
		setValue(COLUMN_PACKAGE_NAME, pkgName);
	}
	
	public String getPackageName() {
		return getTextValue(COLUMN_PACKAGE_NAME);
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
	public String toString() {
		return String.format("%s(0x%08x, id: %d): package(%s), time(%s), duration-sum(%s)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getPackageName(),
				CalendarUtils.timeToReadableString(getTime()),
				CalendarUtils.durationToReadableString(getDurationSum()));
	}

	@Override
	protected String getResourcePackage() {
		return getPackageName();
	}

}
