package com.dailystudio.memory.where.databaseobject;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class IdspotHistorySummary extends IdentifiedLocationObject {

    public static final IntegerColumn COLUMN_IDSPOT_ID = 
            new IntegerColumn("idspot_id", false);

    public static final Column COLUMN_SUM_DURATION = 
    		new LongColumn("sum( " + IdspotHistory.COLUMN_DURATION.getName() + " )");

	public static final Column COLUMN_OCCURRENCE = 
			new IntegerColumn("count( " + IdspotHistory.COLUMN_ID.getName() + " )");

    private final static Column[] sCloumns = {
    	COLUMN_IDSPOT_ID,
        COLUMN_SUM_DURATION,
        COLUMN_OCCURRENCE,
    };
    
    public IdspotHistorySummary(Context context) {
        super(context);
        
        final Template templ = getTemplate();
        
        templ.addColumns(sCloumns);
    }

	public int getIdentifiedHotspotId() {
		return getIntegerValue(COLUMN_IDSPOT_ID);
	}

	public void setIdentifiedHotspotId(int idspotId) {
		setValue(COLUMN_IDSPOT_ID, idspotId);
	}

	public void setSumDuration(long duration) {
		setValue(COLUMN_SUM_DURATION, duration);
	}

	public long getSumDuration() {
		return getLongValue(COLUMN_SUM_DURATION);
	}

	public void setOccurrence(int occurrence) {
		setValue(COLUMN_OCCURRENCE, occurrence);
	}

	public int getOccurrence() {
		return getIntegerValue(COLUMN_OCCURRENCE);
	}
	
    @Override
    public String toString() {
        return String.format("%s, idspotId(%d), sum-duration(%s), occurrence(%d)",
                super.toString(),
                getIdentifiedHotspotId(),
                CalendarUtils.durationToReadableString(getSumDuration()),
                getOccurrence());
    }

}
