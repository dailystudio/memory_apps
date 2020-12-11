package com.dailystudio.memory.where;

import android.content.Context;

import com.dailystudio.memory.task.Task;
import com.dailystudio.memory.where.lifestyle.WorkAndSpareTimeChecker;

public class WorkAndSpareTimeCheckTask extends Task {

	public WorkAndSpareTimeCheckTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		WorkAndSpareTimeChecker checker = 
		        new WorkAndSpareTimeChecker(context);
		
		checker.runIfOnTime();
	}

}
