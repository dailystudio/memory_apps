package com.dailystudio.memory.application;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.text.TextUtils;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.UsageDatabaseModal;
import com.dailystudio.memory.task.Task;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;
import com.dailystudio.nativelib.observable.ScreenOnOffObservable;

public class ActivityUsageTask extends Task {

	public ActivityUsageTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		checkAndUpdateUsage();
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
		checkAndUpdateUsage();
	}
	
	private void checkAndUpdateUsage() {
		final boolean screenOn = isScreenOn();
		
		if (screenOn) {
			final ComponentName topActivity = getTopActivityNative();

			UsageDatabaseModal.markActivityUsageStart(
					mContext, topActivity);
			
			scheduleNextCollection();
		} else {
			UsageDatabaseModal.markCurrentActivityUsageEnd(mContext);
		}
	}
	
	private void scheduleNextCollection() {
		final Resources res = mContext.getResources();
		if (res == null) {
			return;
		}
		
		final int interval = 
			res.getInteger(R.integer.config_activity_usage_task_peroid);
		
		scheduleExecuteDelayed(interval);
	}
	
	private boolean isScreenOn() {
		PowerManager pwmgr =
			(PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		if (pwmgr == null) {
			return false;
		}
		
		return pwmgr.isScreenOn();
	}
	
	private ComponentName getTopActivityNative() {
		return getTopActivityAboveL();
	}
	
	@SuppressWarnings("unused")
	private ComponentName getTopActivityBelowL() {
		ActivityManager actmgr =
				(ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			if (actmgr == null) {
				return null;
			}
			
			List<RunningTaskInfo> tasks = actmgr.getRunningTasks(1);
			if (tasks == null || tasks.size() <= 0) {
				return null;
			}
			
//			PrintUtils.printTasks(tasks);
			
			RunningTaskInfo topTask = tasks.get(0);
			
			return topTask.topActivity;
	}
	
	private ComponentName getTopActivityAboveL() {
		ActivityManager actmgr =
				(ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			if (actmgr == null) {
				return null;
			}
			
			List<RunningTaskInfo> tasks = actmgr.getRunningTasks(1);
			if (tasks == null || tasks.size() <= 0) {
				return null;
			}
			
			List<RunningAppProcessInfo> processes = actmgr.getRunningAppProcesses();
//			PrintUtils.printProcesses(processes);
			if (processes == null) {
				return null;
			}
			
			RunningAppProcessInfo p0 = processes.get(0);
			if (p0 == null) {
				return null;
			}
			
			String pkg = p0.processName;
			
			String[] pkglist = p0.pkgList;
			if (pkglist != null && pkglist.length > 0) {
				pkg = pkglist[0];
			}
			
			Logger.debug("top app-pkg: %s", pkg);
			if (TextUtils.isEmpty(pkg)) {
				return null;
			}
			
			AndroidApplication app = new AndroidApplication(pkg);
			
			Intent i = app.getLaunchIntent(mContext);
			if (i == null) {
				return null;
			}
			
			final ComponentName comp = i.getComponent();
			Logger.debug("top app-comp: %s", comp);
			
			return comp;
	}
	
	private Observer mObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			checkAndUpdateUsage();
		}
		
	};

}
