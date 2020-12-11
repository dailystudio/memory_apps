package com.dailystudio.memory.boot;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.task.Task;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;
import com.dailystudio.nativelib.observable.ScreenOnOffObservable;

public class ScreenOnOffTask extends Task {

	public ScreenOnOffTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		checkAndUpdateScreenOnOff();
	}

	@Override
	public void onDestroy(Context context, long time) {
		super.onDestroy(context, time);
	}
	
	@Override
	public void onPrepareObservables(Context context, long time) {
		super.onPrepareObservables(context, time);
		
		NativeObservable observable = 
			ObservableManager.getObservable(ScreenOnOffObservable.class);
		if (observable != null) {
			Logger.debug("observable = %s, count() = %d", 
					observable, observable.countObservers());
			Logger.debug("mObserver = %s", mObserver);
			observable.addObserver(mObserver);
		}
	}
	
	@Override
	public void onDestoryObservables(Context context, long time) {
		super.onDestoryObservables(context, time);
		
		NativeObservable observable = 
			ObservableManager.getObservable(ScreenOnOffObservable.class);
		if (observable != null) {
			observable.deleteObserver(mObserver);
			Logger.debug("observable = %s, count() = %d", 
					observable, observable.countObservers());
			Logger.debug("mObserver = %s", mObserver);
		}
	}

	@Override
	public void onExecute(Context context, long time) {
		checkAndUpdateScreenOnOff();
	}
	
	private void checkAndUpdateScreenOnOff() {
		MemoryScreenOnDatabaseModal.checkScreenOnOrOff(mContext);
	}

	private Observer mObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			checkAndUpdateScreenOnOff();
		}
		
	};

}
