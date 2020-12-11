package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public abstract class UsageStatistics extends CachedResMemoryObject {
	
	public static final Column COLUMN_DURATION_SUM = 
		new LongColumn("sum(" + Usage.COLUMN_DURATION.getName() + ")");
	
	private final static Column[] sCloumns = {
		COLUMN_DURATION_SUM,
	};
	
	public UsageStatistics(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public long getDurationSum() {
		return getLongValue(COLUMN_DURATION_SUM);
	}

	public void setDurationSum(long sum) {
		setValue(COLUMN_DURATION_SUM, sum);
	}
	
}
