package com.dailystudio.memory.boot;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnShutdownReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Context appContext = context.getApplicationContext();
		
		GlobalContextWrapper.bindContext(appContext);
		
        if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
        	MemoryBoot boot = MemoryBootDatabaseModal.getLastBoot(context);
        	if (MemoryBootDatabaseModal.isCurrentBoot(appContext, boot) == false) {
        		return;
        	}
        	
        	long now = System.currentTimeMillis();
        	Logger.debug("downTime(%d)", now);
        	Logger.debug("shutdownTime(%d, %s)", 
        			now, CalendarUtils.timeToReadableString(now));
        	
        	MemoryBootDatabaseModal.tagALiveForBoot(appContext, boot);
        }
	}
	
}
