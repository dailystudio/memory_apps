package com.dailystudio.memory.where.databaseobject;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TimeColumn;

import android.content.Context;

public class OldIdentifiedHotspot extends IdentifiedHotspot {
    
	public static final TimeColumn COLUMN_DELETE_TIME = new TimeColumn("delete_time", false);

	private final static Column[] sColumns = {
		COLUMN_DELETE_TIME,
	};

    public OldIdentifiedHotspot(Context context) {
        super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sColumns);
	}
    
	public void setDeleteTime(long time) {
		setValue(COLUMN_DELETE_TIME, time);
	}
	
	public long getDeleteTime() {
		return getLongValue(COLUMN_DELETE_TIME);
	}

}
