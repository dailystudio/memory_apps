package com.dailystudio.memory;

import java.util.Observable;
import java.util.Observer;

import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.development.Logger;
import com.dailystudio.manager.Manager;
import com.dailystudio.memory.game.MemoryGameService;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;
import com.dailystudio.memory.loader.AppChangedLoader;
import com.dailystudio.memory.loader.InitLoader;
import com.dailystudio.memory.loader.LoaderController;
import com.dailystudio.memory.notify.MemoryNotify;
import com.dailystudio.memory.plugin.PluginManager;
import com.dailystudio.memory.plugin.PluginObserverable;
import com.dailystudio.memory.task.TaskHostManager;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

public class MemoryService extends Service {

	private final static int NOTIFY_ID_SERVICE = 0x8283;
	
	public static final Intent SERVICE_INTENT = 
		new Intent("com.dailystudio.dailylife.SERVICE");

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		/* 
		 * XXX: call this will make ThumbCacheManager
		 * 		created in main thread to avoid thread access
		 * 		issue.
		 */
		ThumbCacheManager.clear();
		
		initService();
		
		startAchivementSerivce();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		Logger.debug("newConfig = %s", newConfig);
		reinitService();
	}
	
	private void initService() {
		final Context context = getApplicationContext();
		
		MemoryBoot lastBoot = MemoryBootDatabaseModal.getLastBoot(context);
		if (lastBoot == null) {
			MemoryBootDatabaseModal.tagNewBoot(context, 0);
		}
		
		initObservables();
		
		LoaderController.startLoader(new InitLoader(this));
		
//		MemoryNotify.notifySystemInfo(context,
//				NOTIFY_ID_SERVICE,
//				getString(R.string.notification_content),
//				new Intent(context, MainActivity.class),
//				Notification.FLAG_ONGOING_EVENT);
        MemoryNotify.beginForegroundService(this,
                NOTIFY_ID_SERVICE,
                getString(R.string.notification_content),
				new Intent(context, MainActivity.class));
	}
	
	private void reinitService() {
		final Context context = getApplicationContext();
		
//		MemoryNotify.cancelNotifySystemInfo(context,
//				NOTIFY_ID_SERVICE);
        stopForeground(true);
		
		LoaderController.startLoader(new InitLoader(this));
		
//		MemoryNotify.notifySystemInfo(context,
//				NOTIFY_ID_SERVICE,
//				getString(R.string.notification_content),
//				new Intent(context, MainActivity.class),
//				Notification.FLAG_ONGOING_EVENT);
        MemoryNotify.beginForegroundService(this,
                NOTIFY_ID_SERVICE,
                getString(R.string.notification_content),
                new Intent(context, MainActivity.class));
	}
	
	private void initObservables() {
		NativeObservable observable = null;
		
		observable = ObservableManager.getObservable(AppObservable.class);
		Logger.debug("observable: %s", observable);
		if (observable != null) {
			observable.addObserver(mAppObserver);
		}
		
		observable = ObservableManager.getObservable(PluginObserverable.class);
		if (observable != null) {
			Logger.debug("PluginObserverable: %s created in main loop", observable);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
//		MemoryNotify.cancelNotifySystemInfo(getApplicationContext(),
//				NOTIFY_ID_SERVICE);
        stopForeground(true);
		
		PluginManager.clearPlugins();
		TaskHostManager.clearTasks();
		
		Manager.clearManagers();
		
		ThumbCacheManager.clear();
		
		stopAchivementSerivce();
	}
	
	private void startAchivementSerivce() {
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), MemoryGameService.class);
		
		startService(i);
	}

    private void stopAchivementSerivce() {
		Intent i = new Intent();
		
		i.setClass(getApplicationContext(), MemoryGameService.class);
		
		stopService(i);
	}

	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable arg0, Object arg1) {
			if (arg1 instanceof Intent == false) {
				return;
			}
			
			LoaderController.startLoader(
					new AppChangedLoader(MemoryService.this, (Intent)arg1));
		}
		
	};
	
}
