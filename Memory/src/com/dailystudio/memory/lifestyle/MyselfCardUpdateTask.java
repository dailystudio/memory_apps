package com.dailystudio.memory.lifestyle;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.person.PersonFeatureObservable;
import com.dailystudio.memory.task.Task;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class MyselfCardUpdateTask extends Task {
	
	public MyselfCardUpdateTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		checkAndUpdateCard();
	}
	
	@Override
	public void onPrepareObservables(Context context, long time) {
		super.onPrepareObservables(context, time);
		
		NativeObservable observable = 
			ObservableManager.getObservable(PersonFeatureObservable.class);
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
			ObservableManager.getObservable(PersonFeatureObservable.class);
		if (observable != null) {
			observable.deleteObserver(mObserver);
			Logger.debug("observable = %s, count() = %d", 
					observable, observable.countObservers());
			Logger.debug("mObserver = %s", mObserver);
		}
	}

	@Override
	public void onExecute(Context context, long time) {
		checkAndUpdateCard();
	}
	
	private void checkAndUpdateCard() {
		Logger.debug("start to update myself card");
	}

	private Observer mObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			checkAndUpdateCard();
		}
		
	};

}
