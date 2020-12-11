package com.dailystudio.memory.boot;

import android.content.Context;
import android.os.SystemClock;

import com.dailystudio.app.dataobject.DatabaseWriter;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;

public class MemoryBootDatabaseModal {
	
	private static final long BOOT_DELTA_ACCEPTED = 30000;
	
	public static long getCurrentBootSeqeunce(Context context) {
		MemoryBoot lastBoot = getLastBoot(context);
		if (lastBoot == null) {
			return -1;
		}
		
		return lastBoot.getBootSequence();
	}

	public static synchronized MemoryBoot getLastBoot(Context context) {
		if (context == null) {
			return null;
		}

		TimeCapsuleDatabaseReader<MemoryBoot> reader =
			new TimeCapsuleDatabaseReader<MemoryBoot>(context,
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		final MemoryBoot lastBoot = reader.queryLastOne();

		return lastBoot;
	}
	
	public static synchronized MemoryBoot getBoot(Context context, long bootseq) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryBoot> reader =
			new TimeCapsuleDatabaseReader<MemoryBoot>(context,
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		Query query = new Query(MemoryBoot.class);

		ExpressionToken selToken = 
			MemoryBoot.COLUMN_BOOT_SEQUENCE.eq(bootseq);
		if (selToken == null) {
			return null;
		}
		query.setSelection(selToken);
		
		OrderingToken orderByToken = 
			MemoryBoot.COLUMN_TIME.orderByDescending();
		if (orderByToken == null) {
			return null;
		}
		query.setOrderBy(orderByToken);

		query.setLimit(new ExpressionToken(1));
		
		final MemoryBoot boot = reader.queryLastOne(query);
		
		return boot;
	}

	private static long estimateBootTime(long now) {
		final long elapsed = SystemClock.elapsedRealtime();
		final long bootTime = now - elapsed; 
		return bootTime;
	}

	public static synchronized boolean isCurrentBoot(Context context, MemoryBoot boot) {
		if (context == null || boot == null) {
			return false;
		}
		
		MemoryBoot lastBoot = getLastBoot(context);
		if (lastBoot == null) {
			return false;
		}
		
		final long now = System.currentTimeMillis();

		final long estimatedBootTime = estimateBootTime(now); 
		final long bootTime = boot.getTime();
		
		final long delta = estimatedBootTime - bootTime;
/*		Logger.debug("DELTA: APPRO(%d) - ACCUR(%d) = %d millis",
				estimatedBootTime, bootTime, delta);
*/
		return (Math.abs(delta) < BOOT_DELTA_ACCEPTED);
	}

	public static synchronized boolean tagNewBoot(Context context, long bootTime) {
		return tagNewBoot(context, bootTime, 0);
	}
	
	public static synchronized boolean tagNewBoot(Context context, long bootTime, long upTime) {
		Logger.debug("bootTime = %s", CalendarUtils.timeToReadableString(bootTime));
		Logger.debug("upTime = %s", CalendarUtils.durationToReadableString(upTime));
		if (context == null) {
			return true;
		}
		
		if (bootTime <= 0) {
			final long now = System.currentTimeMillis();
			final long elapsed = SystemClock.elapsedRealtime();
			
			bootTime = now - elapsed;
		}

		final MemoryBoot lastBoot = getLastBoot(context);
		Logger.debug("lastBoot(%s)", lastBoot);

		long bootseq = 0;
		long lastShutdown = 0;
		
		if (lastBoot != null) {
			bootseq = lastBoot.getBootSequence() + 1;
			
			lastShutdown = lastBoot.getShutDownTime();
		} 
		
		final boolean estimated = (bootTime <= lastShutdown);
		
		MemoryBoot newBoot = new MemoryBoot(context);
		
		newBoot.setTime(bootTime);
		newBoot.setBootSequence(bootseq);
		newBoot.setBootUpTime(upTime);
		newBoot.setEsitmated(estimated);
		
		Logger.debug("newBoot = %s", newBoot);
		DatabaseWriter<MemoryBoot> writer = 
			new DatabaseWriter<MemoryBoot>(context, 
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		writer.insert(newBoot);
		
		return estimated;
	}

	public static synchronized void tagALiveForBoot(Context context, MemoryBoot boot) {
		if (context == null) {
			return;
		}
		
		if (boot == null) {
			return;
		}
		
		final long bootseq = boot.getBootSequence() + 1;
		if (bootseq < 0) {
			return;
		}
		
		final long downTime = System.currentTimeMillis();
		
		boot.setShutDownTime(downTime);
		
		TimeCapsuleDatabaseWriter<MemoryBoot> writer =
			new TimeCapsuleDatabaseWriter<MemoryBoot>(context,
					Constants.MEMORY_DATABASE_AUTHORITY,
					MemoryBoot.class);
		
		writer.update(boot);
	}

}
