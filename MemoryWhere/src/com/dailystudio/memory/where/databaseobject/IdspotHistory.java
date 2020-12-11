package com.dailystudio.memory.where.databaseobject;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class IdspotHistory extends IdentifiedLocationObject {

    public static final IntegerColumn COLUMN_IDSPOT_ID = 
            new IntegerColumn("idspot_id", false);

    public static final Column COLUMN_BOOT_SEQUENCE = new LongColumn("bootseq", false);
	
    public static final LongColumn COLUMN_DURATION = 
            new LongColumn("duration");

    private final static Column[] sCloumns = {
    	COLUMN_IDSPOT_ID,
    	COLUMN_BOOT_SEQUENCE,
        COLUMN_DURATION,
    };
    
    public IdspotHistory(Context context) {
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

	public long getBootSequence() {
		return getLongValue(COLUMN_BOOT_SEQUENCE);
	}

	public void setBootSequence(long seqnum) {
		setValue(COLUMN_BOOT_SEQUENCE, seqnum);
	}

    public void setDuration(long duration) {
        setValue(COLUMN_DURATION, duration);
    }

    public long getDuration() {
        return getLongValue(COLUMN_DURATION);
    }

    @Override
    public String toString() {
        return String.format("%s, idspotId(%d), seq(%d), duration(%s)",
                super.toString(),
                getIdentifiedHotspotId(),
				getBootSequence(),
                CalendarUtils.durationToReadableString(getDuration()));
    }

}
