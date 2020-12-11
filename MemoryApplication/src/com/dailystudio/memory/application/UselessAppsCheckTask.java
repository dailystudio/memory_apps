package com.dailystudio.memory.application;

import android.content.Context;

import com.dailystudio.memory.application.appchecker.UselessAppsChecker;
import com.dailystudio.memory.task.Task;

public class UselessAppsCheckTask extends Task {

	public UselessAppsCheckTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onDestroy(Context context, long time) {
		super.onDestroy(context, time);
	}
	
	@Override
	public void onExecute(Context context, long time) {
		UselessAppsChecker checker = new UselessAppsChecker();
		
		checker.checkAndFindUselessApps(mContext);
	}

}
