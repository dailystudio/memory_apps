package com.dailystudio.memory.loader;

import android.content.Context;

public abstract class MemoryLoaderStep {

	protected Context mContext;
	
	private MemoryStepLoader mLoader;
	
	public MemoryLoaderStep(Context context) {
		mContext = context;
		
		mLoader = null;
	}
	
	final void attachToLoader(MemoryStepLoader loader) {
		if (loader == null) {
			return;
		}
		
		mLoader = loader;
	}
	
 	final protected void postAndWait(Runnable r) {
 		if (r == null) {
 			return;
 		}
 		
 		if (mLoader == null) {
 			return;
 		}
 		
 		mLoader.postAndWait(r);
 	}

 	abstract public boolean loadInBackground();
 	
}
