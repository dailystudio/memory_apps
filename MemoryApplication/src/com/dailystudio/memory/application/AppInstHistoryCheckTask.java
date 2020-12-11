package com.dailystudio.memory.application;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.appchecker.AppInstHistoryChecker;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.task.Task;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class AppInstHistoryCheckTask extends Task {

	public AppInstHistoryCheckTask(Context context) {
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
	public void onPrepareObservables(Context context, long time) {
		super.onPrepareObservables(context, time);
		
		NativeObservable observable = 
			ObservableManager.getObservable(AppObservable.class);
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
			ObservableManager.getObservable(AppObservable.class);
		if (observable != null) {
			observable.deleteObserver(mObserver);
			Logger.debug("observable = %s, count() = %d", 
					observable, observable.countObservers());
			Logger.debug("mObserver = %s", mObserver);
		}
	}

	@Override
	public void onExecute(Context context, long time) {
		AppInstHistoryChecker checker = new AppInstHistoryChecker();
		
		checker.checkAndUpdateAllAppInstHistory(context);
	}
	
	private Observer mObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			if (arg1 instanceof Intent == false) {
				return;
			}
			
			Intent i = (Intent)arg1;
			final String action = i.getAction();
			if (action == null) {
				return;
			}
			
			final Uri data = i.getData();
			if (data == null) {
				return;
			}
			
			final String pkgName = data.getSchemeSpecificPart();

			final boolean replacing = i.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			Logger.debug("replacing = %s", replacing);
			
			AppInstHistory history = new AppInstHistory(mContext);
			
			history.setTime(System.currentTimeMillis());
			history.setPackageName(pkgName);
						
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				if (replacing) {
					history.setPackageAction(AppInstHistory.PACKAGE_ACTION_UPDATE);
				} else {
					history.setPackageAction(AppInstHistory.PACKAGE_ACTION_INSTALL);
				}
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				if (!replacing) {
					history.setPackageAction(AppInstHistory.PACKAGE_ACTION_UNINSTALL);
				}
			} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
				history.setPackageAction(AppInstHistory.PACKAGE_ACTION_UPDATE);
			} else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
				history.setPackageAction(AppInstHistory.PACKAGE_ACTION_UPDATE);
			}

			Logger.debug("ACTION: %s, PKG:%s, history = %s", 
					action,
					pkgName,
					history);
			if (history.getPackageAction() != null) {
				AppInstHistoryChecker checker = new AppInstHistoryChecker();

				checker.updateSingleAppInstHistory(mContext, history);
			}
		}
		
	};

}
