package com.dailystudio.memory.application.appchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.application.databaseobject.AppInstHistoryDatabaseModal;
import com.dailystudio.nativelib.application.AndroidApplication;

public class AppInstHistoryChecker extends AbsAppsChecker {
	
	private final static String APP_INST_PREF = "app-inst-pref";
	private final static String KEY_APP_INST_CHECK_TIMESTAMP = "app-inst-check-timestamp";
	
	private class RefreshAppInstHistoryAsyncTask 
		extends AbsAppsChecker.BaseCheckAsyncTask {
		
		private RefreshAppInstHistoryAsyncTask(Context context) {
			super(context);
		}
		
		@Override
		protected void doAppsCheck(long now, long lastTimestamp) {
			List<AndroidApplication> apps = 
					AndroidApplication.queryApplications(mContext);
			if (apps == null) {
				return;
			}
			
			List<AppInstHistory> toCacheResources = 
					new ArrayList<AppInstHistory>();
			
			Set<String> toRemoved = 
					AppInstHistoryDatabaseModal.getPackagesHasHistories(mContext);
			
			AppInstHistory history = null;
			
			for (AndroidApplication app: apps) {
				if (app.isSystem()) {
					continue;
				}
				
				if (toRemoved != null) {
					toRemoved.remove(app.getPackageName());
				}
				
				if (app.getFirstInstallTime() > lastTimestamp) {
					history = AppInstHistoryDatabaseModal.addInstallHistory(mContext, app);
					if (history != null) {
						toCacheResources.add(history);
					}
					
					if (app.getFirstInstallTime() == app.getLastUpdateTime()) {
						Logger.warnning("Skip add UPDATE action of pkg[%s], becaue time is same" ,
								app.getPackageName());
						continue;
					}

					AppInstHistoryDatabaseModal.addUpdateHistory(mContext, app);
				} else if (app.getLastUpdateTime() > lastTimestamp) {
					AppInstHistoryDatabaseModal.addUpdateHistory(mContext, app);
				}
			}
			
			if (toRemoved != null) {
				for (String pkg: toRemoved) {
					Logger.debug("toRemoved.pkg = %s", pkg);

					history = AppInstHistoryDatabaseModal.getLatestHistory(
							mContext, pkg);
					Logger.debug("toRemoved.history = %s", history);
					if (history == null 
							|| (!history.getPackageAction().equals(AppInstHistory.PACKAGE_ACTION_UNINSTALL))) {
						AppInstHistoryDatabaseModal.addUninstallHistory(
								mContext, now, pkg);
					}
				}
			}
			
			if (toCacheResources != null) {
				for (AppInstHistory aih: toCacheResources) {
					aih.cacheAppResources();
				}
			}
		}
		
	}
	
	private class SingleAppInstHistoryUpdateAsyncTask 
		extends AbsAppsChecker.BaseCheckAsyncTask {
	
		private AppInstHistory mHistory;
		
		private SingleAppInstHistoryUpdateAsyncTask(Context context, AppInstHistory history) {
			super(context);
			
			mHistory = history;
		}
	
		@Override
		protected void doAppsCheck(long now, long lastTimestamp) {
			if (mHistory == null) {
				return;
			}
			
			final DatabaseConnectivity connectivity = 
					new DatabaseConnectivity(mContext, 
							AppInstHistory.class);
			
			if (!AppInstHistoryDatabaseModal.containsHistory(mContext, mHistory)) {
				connectivity.insert(mHistory);
			}
			
			if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(mHistory.getPackageAction())) {
				mHistory.cacheAppResources();
			}
		}
		
	}
	
	public void checkAndUpdateAllAppInstHistory(Context context) {
		if (context == null) {
			return;
		}
		
		final long now = System.currentTimeMillis();

		final long lastTimestamp = getLastCheckTimestamp(context);
		Logger.debug("lastTimestamp = %d(%s), current = %d(%s)", 
				lastTimestamp,
				CalendarUtils.timeToReadableString(lastTimestamp),
				now,
				CalendarUtils.timeToReadableString(now));
		if (lastTimestamp == -1
				|| (now - lastTimestamp >= CalendarUtils.DAY_IN_MILLIS)) {
			pendingOrExecuteTask(new RefreshAppInstHistoryAsyncTask(context));
		}
	}
	
	public void updateSingleAppInstHistory(Context context, AppInstHistory history) {
		if (context == null || history == null) {
			return;
		}
		
		pendingOrExecuteTask(new SingleAppInstHistoryUpdateAsyncTask(
				context, history));
	}

	@Override
	protected String getCheckPrefName(Context context) {
		return APP_INST_PREF;
	}

	@Override
	protected String getCheckTimestampKey(Context context) {
		return KEY_APP_INST_CHECK_TIMESTAMP;
	}
	
}
