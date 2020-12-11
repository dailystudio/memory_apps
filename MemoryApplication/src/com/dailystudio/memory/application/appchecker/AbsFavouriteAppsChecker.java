package com.dailystudio.memory.application.appchecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.ActivityUsageStatistics;
import com.dailystudio.memory.application.databaseobject.ApplicationUsageStatistics;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.databaseobject.FavoriteAppDatabaseModal;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.application.ui.UsageStatisticsComparator;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.content.Context;

public abstract class AbsFavouriteAppsChecker extends PeroidicalAsyncChecker {
	
	private int mFavourtieClass;
	
	public AbsFavouriteAppsChecker(Context context, int favouriteClass) {
		super(context);
		
		mFavourtieClass = favouriteClass;
	}
	
	public int getFavouriteClass() {
		return mFavourtieClass;
	}

	@Override
	public long getCheckInterval() {
		return CalendarUtils.DAY_IN_MILLIS;
	}

	@Override
	protected void doCheck(long now, long lastTimestamp) {
		final Query query = getCheckQuery();
		if (query == null) {
			return;
		}
		
		TimeCapsuleDatabaseReader<Usage> reader =
				new TimeCapsuleDatabaseReader<Usage>(mContext, Usage.class);
		
		List<DatabaseObject> objects = 
				reader.query(query, ActivityUsageStatistics.class);
		if (objects == null) {
			return;
		}
		
		List<UsageStatistics> actstats = new ArrayList<UsageStatistics>();
		for (DatabaseObject o: objects) {
			if (o instanceof ActivityUsageStatistics) {
//				Logger.debug("ua = %s", o);
				actstats.add((UsageStatistics)o);
			}
		}
		
		List<UsageStatistics> results = 
				ActivityUsageStatistics.convertToApplicationUsageStatistics(
						mContext, actstats);
		if (results == null) {
			return;
		}

		Collections.sort(results, new UsageStatisticsComparator());
		
		List<FavoriteApp> fApps = new ArrayList<FavoriteApp>();
		
		String pkgName;
		FavoriteApp app;
		for (UsageStatistics us: results) {
			if (us instanceof ApplicationUsageStatistics == false) {
				continue;
			}
			
			pkgName = ((ApplicationUsageStatistics)us).getPackageName();
			if (AndroidApplication.isDefaultLauncherApp(mContext, pkgName)) {
				Logger.debug("SKIP launch app: %s", pkgName);
				continue;
			}
			
			app = new FavoriteApp(mContext);
			
			app.setTime(System.currentTimeMillis());
			app.setFavoriteClass(mFavourtieClass);
			app.setPackageName(pkgName);
			app.setSystemApp(AndroidApplication.isSystemApplication(
					mContext, pkgName));
			app.setLocked(false);
			
			fApps.add(app);
			Logger.debug("Favorite App found: %s", app);
		}
		
		if (fApps.size() <= 0) {
			return;
		}
		
		FavoriteAppDatabaseModal.clearFavoriteApps(mContext, mFavourtieClass);
		
		TimeCapsuleDatabaseWriter<FavoriteApp> writer =
				new TimeCapsuleDatabaseWriter<FavoriteApp>(
						mContext, FavoriteApp.class);
		
		writer.insert(fApps.toArray(new FavoriteApp[0]));
	}

	abstract Query getCheckQuery();

}
