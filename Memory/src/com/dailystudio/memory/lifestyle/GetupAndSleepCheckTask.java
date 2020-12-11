package com.dailystudio.memory.lifestyle;

import android.content.Context;

import com.dailystudio.memory.boot.lifestyle.GetupAndSleepChecker;
import com.dailystudio.memory.task.Task;

public class GetupAndSleepCheckTask extends Task {

	public GetupAndSleepCheckTask(Context context) {
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
		GetupAndSleepChecker checker = 
				new GetupAndSleepChecker(mContext);
		
		checker.runIfOnTime();
	}
	

}
