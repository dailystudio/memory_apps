package com.dailystudio.memory.database;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class MemoryPluginActivityCategoryObject extends MemoryUsageCountObject {
	
	public static final Column COLUMN_CATEGORY = new TextColumn("category", false);
	
	private final static Column[] sCloumns = {
		COLUMN_CATEGORY,
	};

	public MemoryPluginActivityCategoryObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}

	public String getCategory() {
		return getTextValue(COLUMN_CATEGORY);
	}
	
	public void setCategory(String category) {
		setValue(COLUMN_CATEGORY, category);
	}
	
	@Override
	public String toString() {
		return String.format("%s, category = %s",
				super.toString(),
				getCategory());
	}

}
