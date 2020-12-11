package com.dailystudio.memory.boot;

import android.content.Context;
import android.content.Intent;

import com.dailystudio.memory.boot.record.MemoryBootRecordDatabaseModal;
import com.dailystudio.memory.boot.record.Records;
import com.dailystudio.memory.game.MemoryGameUtils;
import com.dailystudio.memory.plugin.MemoryPlugin;
import com.dailystudio.memory.record.MemoryRecord;

public class PluginBoot extends MemoryPlugin {

	@Override
	protected boolean onRegister(Context context, long now) {
		boolean ret = super.onRegister(context, now);
		
		checkScreenOn(context);
		checkAndUpdateRecords(context);
	
		checkAndUpdateLeaderboard(context);
		
		return ret;
	}

	@Override
	protected Intent getKeepAliveTaskServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), BootTasksKeepAliveSerive.class);

		return i;
	}

	private void checkScreenOn(Context context) {
		MemoryScreenOnDatabaseModal.checkScreenOnOrOff(context);
	}
	
	private void checkAndUpdateRecords(Context context) {
		MemoryRecord fastestUptimeRecrod = 
			MemoryBootRecordDatabaseModal.getRecord(context, 
					Records.RECORD_FASTEST_BOOTUP);
		MemoryRecord slowestUptimeRecrod = 
			MemoryBootRecordDatabaseModal.getRecord(context, 
					Records.RECORD_SLOWEST_BOOTUP);
		MemoryRecord shortestLifetimeRecord = 
				MemoryBootRecordDatabaseModal.getRecord(context, 
						Records.RECORD_SHORTEST_LEFTTIME);
			MemoryRecord longestLifetimeRecord = 
				MemoryBootRecordDatabaseModal.getRecord(context, 
						Records.RECORD_LONGEST_LIFETIME);
		
		if (fastestUptimeRecrod == null 
				|| slowestUptimeRecrod == null
				|| shortestLifetimeRecord == null
				|| longestLifetimeRecord == null) {
			MemoryBootRecordDatabaseModal.checkAndUpdateRecordsBreaking(context);
		}
	}
	
	private void checkAndUpdateLeaderboard(Context context) {
		MemoryRecord fastestUptimeRecrod = 
				MemoryBootRecordDatabaseModal.getRecord(context, 
						Records.RECORD_FASTEST_BOOTUP);
		if (fastestUptimeRecrod == null) {
			return;
		}
		
		MemoryGameUtils.submitLeaderboardScore(context, 
				context.getString(R.string.leaderboard_fastest_bootup), 
				fastestUptimeRecrod.getRecordScore());
	}

}
