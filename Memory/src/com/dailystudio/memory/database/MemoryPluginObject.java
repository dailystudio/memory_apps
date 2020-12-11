package com.dailystudio.memory.database;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class MemoryPluginObject extends MemoryUsageCountObject {

	public static final Column COLUMN_COMPONENT = new TextColumn("component", false);
	
	private final static Column[] sCloumns = {
		COLUMN_COMPONENT,
	};

	public MemoryPluginObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public String getComponent() {
		return getTextValue(COLUMN_COMPONENT);
	}

	public void setComponent(String component) {
		setValue(COLUMN_COMPONENT, component);
	}
	
	@Override
	public String toString() {
		return String.format("%s, comp = %s",
				super.toString(),
				getComponent());
	}

}
