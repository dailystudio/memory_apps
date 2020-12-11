package com.dailystudio.memory.database;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class MemoryUsageCountObject extends MemoryResouceObject {

	public static final Column COLUMN_USAGE_COUNT = new LongColumn("usage_count", false);
	
	private final static Column[] sCloumns = {
		COLUMN_USAGE_COUNT,
	};

	public MemoryUsageCountObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public long getUsageCount() {
		return getLongValue(COLUMN_USAGE_COUNT);
	}
	
	public void setUsageCount(long favorite) {
		setValue(COLUMN_USAGE_COUNT, favorite);
	}
	
	
	@Override
	public String toString() {
		return String.format("%s, usageCount = %d",
				super.toString(),
				getUsageCount());
	}

}
