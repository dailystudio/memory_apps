package com.dailystudio.memory.application.appchecker;

import java.util.List;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.application.databaseobject.UselessAppDatabaseModal;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.content.Context;

public class UselessAppsChecker extends AbsAppsChecker {
	
	private final static String USELESS_APP_PREF = "useless-app-pref";
	private final static String KEY_USELESS_APP_CHECK_TIMESTAMP = "useless-app-check-timestamp";
	
	private final static long APP_NOT_USED_RECENTLY_THRESHOLD = 
			(CalendarUtils.DAY_IN_MILLIS * 30);
	
	private final static long APP_NOT_UPDATED_RECENTLY_THRESHOLD = 
			(CalendarUtils.DAY_IN_MILLIS * 90);
	
	protected class UpdateUslessAppsAsyncTask 
		extends AbsAppsChecker.BaseCheckAsyncTask {
		
		private UpdateUslessAppsAsyncTask(Context context) {
			super(context);
		}
		
		@Override
		protected void doAppsCheck(long now, long lastTimestamp) {
			List<AndroidApplication> apps = 
					AndroidApplication.queryApplications(mContext);
			if (apps == null) {
				return;
			}

			if (now < 0) {
				return;
			}
			
			Usage lastUsage = null;
			long recentUsedTime = -1l;
			long recentUpdatedTime = -1l;
			boolean notUsedRecently = false;
			boolean notUpdatedRecently = false;
			for (AndroidApplication app: apps) {
				if (app.isSystem()) {
					continue;
				}
				
				recentUsedTime = -1l;
				recentUpdatedTime = -1l;
				notUsedRecently = false;
				notUpdatedRecently = false;
				
				lastUsage = getLastUsageForPackage(app.getPackageName());
				if (lastUsage != null) {
					recentUsedTime = (lastUsage.getTime() + lastUsage.getDuration());
				}
				
				recentUpdatedTime = app.getLastUpdateTime();
				
				if (recentUsedTime == -1
						|| (now - recentUsedTime > APP_NOT_USED_RECENTLY_THRESHOLD)) {
					notUsedRecently = true;
				}
				
				if (recentUpdatedTime == -1 
						|| (now - recentUpdatedTime > APP_NOT_UPDATED_RECENTLY_THRESHOLD)) {
					notUpdatedRecently = true;
				}
				
				if (notUsedRecently || notUpdatedRecently) {
					UselessAppDatabaseModal.addUselessApp(mContext, now,
							app.getPackageName(), 
							notUsedRecently, notUpdatedRecently,
							recentUsedTime, recentUpdatedTime);
				} else if (!notUsedRecently && !notUpdatedRecently) {
					UselessAppDatabaseModal.removeUselessApp(mContext, 
							app.getPackageName());
				}
/*				Logger.debug("CHECKED: pkg = %s, notUsedRecently(%s), notUpdated(%s), recent[used: %s, updated:%s]",
						app.getPackageName(),
						notUsedRecently, notUpdatedRecently,
						CalendarUtils.timeToReadableString(recentUsedTime),
						CalendarUtils.timeToReadableString(recentUpdatedTime));
*/
			}
		}
		
		private Usage getLastUsageForPackage(String pkg) {
			if (pkg == null) {
				return null;
			}
			
			final Integer[] compIds = getComponentIds(pkg);
			if (compIds == null) {
				return null;
			}
			
			Query query = new Query(Usage.class);
			ExpressionToken selToken = 
					Usage.COLUMN_COMPONENT_ID.inValues(compIds);
			if (selToken != null) {
				query.setSelection(selToken);
			}
				
			TimeCapsuleDatabaseReader<Usage> reader =
					new TimeCapsuleDatabaseReader<Usage>(
							mContext, Usage.class);
			
			return reader.queryLastOne(query);
		}
		
		private Integer[] getComponentIds(String pkg) {
			if (pkg == null) {
				return null;
			}
			
			final int[] compIds = UsageComponentDatabaseModal.getComponentIds(
					mContext, pkg);
			if (compIds == null) {
				return null;
			}
			
			final int N = compIds.length;
			
			Integer[] result = new Integer[N];
			
			for (int i = 0; i < N; i++) {
				result[i] = Integer.valueOf(compIds[i]);
			}
			
			return result;
		}

	}

	@Override
	protected String getCheckPrefName(Context context) {
		return USELESS_APP_PREF;
	}

	@Override
	protected String getCheckTimestampKey(Context context) {
		return KEY_USELESS_APP_CHECK_TIMESTAMP;
	}

	public void checkAndFindUselessApps(Context context) {
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
			pendingOrExecuteTask(new UpdateUslessAppsAsyncTask(context));
		}
	}
	
}
