package com.dailystudio.memory.loader;

import com.dailystudio.memory.loader.step.CheckAndInitMemoryAskStep;
import com.dailystudio.memory.loader.step.CheckAndPromoteMemoryAppStep;
import com.dailystudio.memory.loader.step.LoadPluginsStep;

import android.content.Context;

public class InitLoader extends MemoryStepLoader {

	public InitLoader(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		addStep(new CheckAndInitMemoryAskStep(mContext));
		addStep(new LoadPluginsStep(mContext));
		addStep(new CheckAndPromoteMemoryAppStep(mContext));
	}

}
