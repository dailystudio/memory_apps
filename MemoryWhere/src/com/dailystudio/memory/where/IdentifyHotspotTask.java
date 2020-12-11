package com.dailystudio.memory.where;

import android.content.Context;

import com.dailystudio.memory.task.Task;
import com.dailystudio.memory.where.hotspot.IdentifiedHotspotChecker;

public class IdentifyHotspotTask extends Task {

	public IdentifyHotspotTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		IdentifiedHotspotChecker checker = 
		        new IdentifiedHotspotChecker(context);
		
		checker.runIfOnTime();
	}

}
