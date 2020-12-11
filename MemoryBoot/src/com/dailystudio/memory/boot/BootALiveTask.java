package com.dailystudio.memory.boot;

import android.content.Context;

import com.dailystudio.memory.task.Task;

public class BootALiveTask extends Task {
	
	public BootALiveTask(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Context context, long time) {
		if (context == null) {
			return;
		}
		
		keepAlive(context, time);
	}

	@Override
	public void onExecute(Context context, long time) {
		keepAlive(context, time);
	}

	private void keepAlive(Context context, long time) {
		if (context == null) {
			return;
		}
		
		MemoryBoot boot = MemoryBootDatabaseModal.getLastBoot(mContext);
		if (boot == null) {
			return;
		}
		
		if (MemoryBootDatabaseModal.isCurrentBoot(context, boot) == false) {
			return;
		}
		
		MemoryBootDatabaseModal.tagALiveForBoot(context, boot);
	}
	
	@Override
	public void onPause(Context context, long time) {
	}

	@Override
	public void onResume(Context context, long time) {
	}
	
}
