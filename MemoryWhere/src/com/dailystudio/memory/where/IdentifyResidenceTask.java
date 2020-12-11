package com.dailystudio.memory.where;

import android.content.Context;

import com.dailystudio.memory.task.Task;
import com.dailystudio.memory.where.hotspot.IdentifiedResidenceChecker;

public class IdentifyResidenceTask extends Task {

	public IdentifyResidenceTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		IdentifiedResidenceChecker checker = 
		        new IdentifiedResidenceChecker(context);
		
		checker.runIfOnTime();
	}

}
