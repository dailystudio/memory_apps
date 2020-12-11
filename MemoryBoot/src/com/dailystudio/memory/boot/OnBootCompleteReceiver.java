package com.dailystudio.memory.boot;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;
import com.dailystudio.memory.boot.record.MemoryBootRecordDatabaseModal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class OnBootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        	long now = System.currentTimeMillis();
        	Logger.debug("now(%d, %s)", now, CalendarUtils.timeToReadableString(now));

        	final long upTime = SystemClock.elapsedRealtime();
        	final long bootTime = (now - upTime);
        	Logger.debug("now(%d) - upTime(%d) = bootTime(%d)", 
        			now, upTime, bootTime);
        	
        	boolean estimated = 
        		MemoryBootDatabaseModal.tagNewBoot(context, bootTime, upTime);
        	
        	if (!estimated) {
	        	MemoryBootRecordDatabaseModal.checkAndUpdateUptimeRecordsBreaking(
	        			context, upTime);
        	}
        }
	}
	
}
