package com.dailystudio.memory.boot;

import android.content.Context;
import android.os.PowerManager;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;

public class MemoryScreenOnDatabaseModal {

	public static MemoryScreenOn getLastScreenOn(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<MemoryScreenOn> reader =
			new TimeCapsuleDatabaseReader<MemoryScreenOn>(context, MemoryScreenOn.class);

		return reader.queryLastOne();
	}

	public static MemoryScreenOn getFirstScreenOn(Context context) {
		if (context == null) {
			return null;
		}
		
		final DatabaseReader<MemoryScreenOn> reader = 
			new DatabaseReader<MemoryScreenOn>(context, MemoryScreenOn.class);
		
		Query query = new Query(MemoryScreenOn.class);
		
		OrderingToken orderByToken = TimeCapsule.COLUMN_TIME.orderByAscending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return reader.queryLastOne(query);
	}

	public static void markScreenOn(Context context) {
		if (context == null) {
			return;
		}
		
		MemoryScreenOn lastScreenOn = getLastScreenOn(context);

		final long lastBootSeq = (lastScreenOn == null ? 
				Constants.INVALID_ID : lastScreenOn.getBootSequence());
		final long currBootSeq = 
			MemoryBootDatabaseModal.getCurrentBootSeqeunce(context);

		if (lastScreenOn != null) {
			if (lastBootSeq == currBootSeq
					&& lastScreenOn.getDuration() == 0) {
				Logger.debug("SKIP: SAME AS lastScreenOn = %s", lastScreenOn);
				return;
			}
			
			markScreenOff(context, lastScreenOn);
		}
		
		MemoryScreenOn usage = new MemoryScreenOn(context);
		
		final long now = System.currentTimeMillis();

		usage.setTime(now);
		usage.setBootSequence(currBootSeq);
		usage.setDuration(0);
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, MemoryScreenOn.class);
		
		connectivity.insert(usage);
	}

	public static void markScreenOff(Context context) {
		final MemoryScreenOn lastScreenOn = getLastScreenOn(context);
		if (lastScreenOn == null) {
			return;
		}
		
		markScreenOff(context, lastScreenOn);
	}
	
	public static void markScreenOff(Context context, MemoryScreenOn screenOn) {
		if (context == null || screenOn == null) {
			return;
		}
		
		Logger.debug("screenOn = %s", screenOn);
		if (screenOn.getDuration() > 0) {
			Logger.debug("SKIP: ALREADY OFF screenOn = %s", screenOn);
			return;
		}
		
		final long usageBootSeq = screenOn.getBootSequence();
		final long currBootSeq = 
			MemoryBootDatabaseModal.getCurrentBootSeqeunce(context);
		
		final long now = System.currentTimeMillis();

		long stopTime = 0;
		if (usageBootSeq != currBootSeq) {
			final MemoryBoot boot = 
				MemoryBootDatabaseModal.getBoot(context, usageBootSeq);
			if (boot != null) {
				stopTime = boot.getShutDownTime();
				if (stopTime <= 0) {
					stopTime = now;
				}
			}
		} else {
			stopTime = now;
		}
		
		long time = screenOn.getTime();

		long duration = stopTime - time;
		if (duration < 0) {
			duration = 0;
		}
		
		screenOn.setDuration(duration);
		
		final TimeCapsuleDatabaseWriter<MemoryScreenOn> writer =
			new TimeCapsuleDatabaseWriter<MemoryScreenOn>(context,
					MemoryScreenOn.class);
		
		writer.update(screenOn);
	}
	
	public static void checkScreenOnOrOff(Context context) {
		final boolean screenOn = isScreenOn(context);
		
		if (screenOn) {
			markScreenOn(context);
		} else {
			markScreenOff(context);
		}
	}
	
	private static boolean isScreenOn(Context context) {
		if (context == null) {
			return false;
		}
		
		PowerManager pwmgr =
			(PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (pwmgr == null) {
			return false;
		}
		
		return pwmgr.isScreenOn();
	}

}
