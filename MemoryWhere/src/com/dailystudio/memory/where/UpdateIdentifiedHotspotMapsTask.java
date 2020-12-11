package com.dailystudio.memory.where;

import android.content.Context;

import com.dailystudio.memory.task.Task;
import com.dailystudio.memory.where.hotspot.IdentifiedHotspotMapsChecker;

public class UpdateIdentifiedHotspotMapsTask extends Task {

	public UpdateIdentifiedHotspotMapsTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		IdentifiedHotspotMapsChecker checker = 
		        new IdentifiedHotspotMapsChecker(context);
		
		checker.runIfOnTime();
	}

}
