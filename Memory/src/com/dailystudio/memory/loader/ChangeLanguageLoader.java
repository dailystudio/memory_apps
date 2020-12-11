package com.dailystudio.memory.loader;

import com.dailystudio.memory.loader.step.LoadPluginsStep;

import android.content.Context;

public class ChangeLanguageLoader extends MemoryStepLoader {

	public ChangeLanguageLoader(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		addStep(new LoadPluginsStep(mContext));
	}

}
