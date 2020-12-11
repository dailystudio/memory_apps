package com.dailystudio.memory.application.databaseobject;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.nativelib.application.AndroidActivity;
import com.dailystudio.nativelib.application.IResourceObject;

public class Usage extends CachedResMemoryObject {
	
	public static final Column COLUMN_BOOT_SEQ = new LongColumn("bootseq", false);
	public static final Column COLUMN_COMPONENT_ID = new IntegerColumn("compid", false);
	public static final LongColumn COLUMN_DURATION = new LongColumn("duration");
	
	private final static Column[] sCloumns = {
		COLUMN_BOOT_SEQ,
		COLUMN_COMPONENT_ID,
		COLUMN_DURATION,
	};
	
	public Usage(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public long getBootSequence() {
		return getLongValue(COLUMN_BOOT_SEQ);
	}

	public void setBootSequence(long seqnum) {
		setValue(COLUMN_BOOT_SEQ, seqnum);
	}
	
	public int getComponentId() {
		return getIntegerValue(COLUMN_COMPONENT_ID);
	}

	public void setComponentId(int compId) {
		setValue(COLUMN_COMPONENT_ID, compId);
	}
		
	public long getDuration() {
		return getLongValue(COLUMN_DURATION);
	}

	public void setDuration(long stoptime) {
		setValue(COLUMN_DURATION, stoptime);
	}
	
	@Override
	protected IResourceObject createResObject() {
		final int compId = getComponentId();
		
		UsageComponent comp = 
			UsageComponentDatabaseModal.getComponent(mContext, compId);
		if (comp == null) {
			return null;
		}
		
		final ComponentName compName = 
			new ComponentName(comp.getPackageName(), 
					comp.getClassName());
		
		return new AndroidActivity(compName);
	}

	@Override
	protected String getResourcePackage() {
		UsageComponent uc = UsageComponentDatabaseModal.getComponent(
				mContext, getComponentId());
		if (uc == null) {
			return null;
		}
		
		return uc.getPackageName();
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): compId(%d), time(%s), duration(%s)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getComponentId(),
				CalendarUtils.timeToReadableString(getTime()),
				CalendarUtils.durationToReadableString(getDuration()));
	}

}
