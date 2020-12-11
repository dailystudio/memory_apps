package com.dailystudio.memory.boot.record;

import java.util.List;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;
import com.dailystudio.memory.boot.NotifyIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.activity.BootRecordsActivity;
import com.dailystudio.memory.notify.MemoryNotify;
import com.dailystudio.memory.record.MemoryRecord;
import com.dailystudio.memory.record.MemoryRecordDatabaseModal;

import android.content.Context;
import android.content.Intent;

public class MemoryBootRecordDatabaseModal extends MemoryRecordDatabaseModal {
	
	public static class MaxAndMinBootupTime extends TimeCapsule {

		public static final LongColumn COLUMN_MIN_BOOTUP = new LongColumn(
				String.format("min(%s)", MemoryBoot.COLUMN_BOOT_UP_TIME.getName()));
		public static final LongColumn COLUMN_MAX_BOOTUP = new LongColumn(
				String.format("max(%s)", MemoryBoot.COLUMN_BOOT_UP_TIME.getName()));

		private final static Column[] sColumns = {
			COLUMN_MIN_BOOTUP,
			COLUMN_MAX_BOOTUP,
		};
		
		public MaxAndMinBootupTime(Context context) {
			super(context);
			
			final Template templ = getTemplate();
			
			templ.addColumns(sColumns);
		}
		
		public long getMinimumBootup() {
			return getLongValue(COLUMN_MIN_BOOTUP);
		}
		
		public long getMaximumBootup() {
			return getLongValue(COLUMN_MAX_BOOTUP);
		}

		@Override
		public String toString() {
			return String.format("%s, bootup[min: %s, max: %s]",
					super.toString(),
					CalendarUtils.durationToReadableString(getMinimumBootup()),
					CalendarUtils.durationToReadableString(getMaximumBootup()));
		}
		
	}
	
	public static class MaxAndMinLifeTime extends TimeCapsule {

		public static final LongColumn COLUMN_MIN_LIFETIME = new LongColumn(
				String.format("min(%s - %s)",
						MemoryBoot.COLUMN_BOOT_SHUTDOWN_TIME.getName(),
						MemoryBoot.COLUMN_TIME.getName()));
		public static final LongColumn COLUMN_MAX_LIFETIME = new LongColumn(
				String.format("max(%s - %s)", 
						MemoryBoot.COLUMN_BOOT_SHUTDOWN_TIME.getName(),
						MemoryBoot.COLUMN_TIME.getName()));

		private final static Column[] sColumns = {
			COLUMN_MIN_LIFETIME,
			COLUMN_MAX_LIFETIME,
		};
		
		public MaxAndMinLifeTime(Context context) {
			super(context);
			
			final Template templ = getTemplate();
			
			templ.addColumns(sColumns);
		}
		
		public long getMinimumLifetime() {
			return getLongValue(COLUMN_MIN_LIFETIME);
		}
		
		public long getMaximumLifetime() {
			return getLongValue(COLUMN_MAX_LIFETIME);
		}

		@Override
		public String toString() {
			return String.format("%s, lifetime[min: %s, max: %s]",
					super.toString(),
					CalendarUtils.durationToReadableString(getMinimumLifetime()),
					CalendarUtils.durationToReadableString(getMaximumLifetime()));
		}
		
	}
	
	public static boolean isBreakingFastestUptimeRecord(Context context, long uptime) {
		MemoryRecord fastest = getRecord(context, Records.RECORD_FASTEST_BOOTUP);
	
		if (fastest == null) {
			return true;
		}
		
		return (uptime < fastest.getRecordScore());
	}
	
	public static boolean isBreakingSlowestUptimeRecord(Context context, long uptime) {
		MemoryRecord slowest = getRecord(context, Records.RECORD_SLOWEST_BOOTUP);
    	Logger.debug("slowest = %s,", slowest);
	
		if (slowest == null) {
			return true;
		}
		
		return (uptime > slowest.getRecordScore());
	}

	public static synchronized MaxAndMinBootupTime getMaxAndMinBootupTime(
			Context context) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryBoot> reader =
			new TimeCapsuleDatabaseReader<MemoryBoot>(context,
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		Query query = new Query(MemoryBoot.class);
		
		ExpressionToken selToken = 
			MemoryBoot.COLUMN_BOOT_UP_TIME.neq(0l)
			.and(MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1));
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		List<DatabaseObject> objects = 
			reader.query(query, MaxAndMinBootupTime.class);

		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		DatabaseObject object0 = objects.get(0);
		if (object0 instanceof MaxAndMinBootupTime == false) {
			return null;
		}
		
		return (MaxAndMinBootupTime)object0;
	}

	public static boolean isBreakingShortestLifetimeRecord(Context context, long lifetime) {
		MemoryRecord shortest = getRecord(context, Records.RECORD_SHORTEST_LEFTTIME);
	
		if (shortest == null) {
			return true;
		}
		
		return (lifetime < shortest.getRecordScore());
	}
	
	public static boolean isBreakingLongestLifetimeRecord(Context context, long lifetime) {
		MemoryRecord longest = getRecord(context, Records.RECORD_LONGEST_LIFETIME);
    	Logger.debug("longest = %s,", longest);
	
		if (longest == null) {
			return true;
		}
		
		return (lifetime > longest.getRecordScore());
	}

	public static synchronized MaxAndMinLifeTime getMaxAndMinLifetime(
			Context context) {
		if (context == null) {
			return null;
		}

		TimeCapsuleDatabaseReader<MemoryBoot> reader =
			new TimeCapsuleDatabaseReader<MemoryBoot>(context,
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		Query query = new Query(MemoryBoot.class);
		
		ExpressionToken selToken = 
				MemoryBoot.COLUMN_BOOT_ESTIMATED.neq(1);
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		List<DatabaseObject> objects = 
			reader.query(query, MaxAndMinLifeTime.class);

		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		DatabaseObject object0 = objects.get(0);
		if (object0 instanceof MaxAndMinLifeTime == false) {
			return null;
		}
		
		return (MaxAndMinLifeTime)object0;
	}

	public static void checkAndUpdateRecordsBreaking(Context context) {
    	if (context == null) {
    		return;
    	}
    	
    	MemoryBoot lastBoot = MemoryBootDatabaseModal.getLastBoot(context);
    	if (lastBoot == null) {
    		return;
    	}
    	
    	final long uptime = lastBoot.getBootUpTime();
    	final long lifetime = (lastBoot.getShutDownTime() - lastBoot.getTime());
    	Logger.debug("uptime = %s, lifetime = %s", 
    			CalendarUtils.durationToReadableString(uptime),
    			CalendarUtils.durationToReadableString(lifetime));
    	
    	checkAndUpdateUptimeRecordsBreaking(context, uptime);
    	checkAndUpdateLifetimeRecordsBreaking(context, lifetime);
	}
	
	public static void checkAndUpdateUptimeRecordsBreaking(Context context, long uptime) {
    	if (context == null) {
    		return;
    	}
    	
    	if (uptime <= 0) {
    		return;
    	}
    	
    	final boolean breakFastestUptime = isBreakingFastestUptimeRecord(
    				context, uptime);
    	final boolean breakSlowestUptime = isBreakingSlowestUptimeRecord(
    				context, uptime);
    	Logger.debug("breakFastestUptime = %s, breakSlowestUptime = %s", 
    			breakFastestUptime, breakSlowestUptime);

    	Intent notifyIntent = new Intent();
    	
    	notifyIntent.setClass(context.getApplicationContext(), 
    			BootRecordsActivity.class);
    	
    	if (breakFastestUptime) {
        	final MemoryRecord record = 
        		MemoryRecordDatabaseModal.getRecord(context, 
        				Records.RECORD_FASTEST_BOOTUP);
        	
        	if (record == null) {
        		MaxAndMinBootupTime maxAndMin = 
        			getMaxAndMinBootupTime(context);
        		
        		Logger.debug("maxAndMin = %s", maxAndMin);
        		
        		if (maxAndMin != null) {
        			uptime = maxAndMin.getMinimumBootup();
        		}
        	}

    		MemoryRecordDatabaseModal.saveNewRecord(
    				context, Records.RECORD_FASTEST_BOOTUP, 
    				uptime);
    		
    		MemoryNotify.notifyInfo(context, 
    				NotifyIds.NOTIFY_BOOTUP_RECORDS, 
    				R.string.notify_title_new_bootup_records, 
    				R.string.notify_contnet_new_fastest_bootup,
    				notifyIntent);
    	}
    	
    	if (breakSlowestUptime) {
        	final MemoryRecord record = 
        		MemoryRecordDatabaseModal.getRecord(context, 
        				Records.RECORD_SLOWEST_BOOTUP);
        	
        	if (record == null) {
        		MaxAndMinBootupTime maxAndMin = 
        			getMaxAndMinBootupTime(context);
        		
        		Logger.debug("maxAndMin = %s", maxAndMin);
        		
        		if (maxAndMin != null) {
        			uptime = maxAndMin.getMaximumBootup();
        		}
        	}
        	
    		MemoryRecordDatabaseModal.saveNewRecord(
    				context, Records.RECORD_SLOWEST_BOOTUP, 
    				uptime);
    		
    		if (!breakFastestUptime) {
	    		MemoryNotify.notifyInfo(context, 
	    				NotifyIds.NOTIFY_BOOTUP_RECORDS, 
	    				R.string.notify_title_new_bootup_records, 
	    				R.string.notify_contnet_new_slowest_bootup,
	    				notifyIntent);
    		}
    	}
	}

	public static void checkAndUpdateLifetimeRecordsBreaking(Context context, long lifetime) {
    	if (context == null) {
    		return;
    	}
    	
    	if (lifetime <= 0) {
    		return;
    	}

    	final boolean breakShortestLifetime = isBreakingShortestLifetimeRecord(
    				context, lifetime);
    	final boolean breakLongestLifetime = isBreakingLongestLifetimeRecord(
    				context, lifetime);
    	Logger.debug("breakShortestLifetime = %s, breakLongestLifetime = %s", 
    			breakShortestLifetime, breakLongestLifetime);

    	if (breakShortestLifetime) {
        	final MemoryRecord record = 
        		MemoryRecordDatabaseModal.getRecord(context, 
        				Records.RECORD_SHORTEST_LEFTTIME);
        	
        	if (record == null) {
        		MaxAndMinLifeTime maxAndMin = 
        			getMaxAndMinLifetime(context);
        		
        		Logger.debug("maxAndMin = %s", maxAndMin);
        		
        		if (maxAndMin != null) {
        			lifetime = maxAndMin.getMinimumLifetime();
        		}
        	}

    		MemoryRecordDatabaseModal.saveNewRecord(
    				context, Records.RECORD_SHORTEST_LEFTTIME, 
    				lifetime);
    	}
    	
    	if (breakLongestLifetime) {
        	final MemoryRecord record = 
        		MemoryRecordDatabaseModal.getRecord(context, 
        				Records.RECORD_LONGEST_LIFETIME);
        	
        	if (record == null) {
        		MaxAndMinLifeTime maxAndMin = 
        			getMaxAndMinLifetime(context);
        		
        		Logger.debug("maxAndMin = %s", maxAndMin);
        		
        		if (maxAndMin != null) {
        			lifetime = maxAndMin.getMaximumLifetime();
        		}
        	}
        	
    		MemoryRecordDatabaseModal.saveNewRecord(
    				context, Records.RECORD_LONGEST_LIFETIME, 
    				lifetime);
    	}
	}

	
}
