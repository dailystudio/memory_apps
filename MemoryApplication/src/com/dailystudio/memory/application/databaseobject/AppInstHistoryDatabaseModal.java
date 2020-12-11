package com.dailystudio.memory.application.databaseobject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.nativelib.application.AndroidApplication;

public class AppInstHistoryDatabaseModal {

	public static AppInstHistory addInstallHistory(Context context,
			AndroidApplication application) {
		return addHistory(context, -1, application, 
				AppInstHistory.PACKAGE_ACTION_INSTALL);
	}
	
	public static AppInstHistory addInstallHistory(Context context, long time,
			AndroidApplication application) {
		return addHistory(context, time, application, 
				AppInstHistory.PACKAGE_ACTION_INSTALL);
	}
	
	public static AppInstHistory addUpdateHistory(Context context,
			AndroidApplication application) {
		return addHistory(context, -1, application, 
				AppInstHistory.PACKAGE_ACTION_UPDATE);
	}
	
	public static AppInstHistory addUpdateHistory(Context context, long time,
			AndroidApplication application) {
		return addHistory(context, time, application, 
				AppInstHistory.PACKAGE_ACTION_UPDATE);
	}
	
	public static AppInstHistory addUninstallHistory(Context context, long time,
			AndroidApplication application) {
		return addHistory(context, time, application, 
				AppInstHistory.PACKAGE_ACTION_UNINSTALL);
	}
	
	public static AppInstHistory addUninstallHistory(Context context, long time,
			String packageName) {
		return addHistory(context, time, packageName, 
				AppInstHistory.PACKAGE_ACTION_UNINSTALL);
	}
	
	public static AppInstHistory addHistory(Context context,
			AndroidApplication application, String action) {
		return addHistory(context, -1, application, action);
	}
	
	public static AppInstHistory addHistory(Context context, long time,
			AndroidApplication application, String action) {
		if (context == null 
				|| application == null
				|| action == null) {
			return null;
		}
		
		final String packageName = application.getPackageName();
		if (packageName == null) {
			return null;
		}	
		
		if (time < 0) {
			if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(action)) {
				time = application.getFirstInstallTime();
			} else if (AppInstHistory.PACKAGE_ACTION_UPDATE.equals(action)) {
				time = application.getLastUpdateTime();
			} else {
				time = -1;
			}
		}
		
		return addHistory(context, time, packageName, action);
	}
	
	public static AppInstHistory addHistory(Context context, long time,
			String packageName, String action) {
		if (context == null 
				|| packageName == null
				|| action == null) {
			return null;
		}
		
		if (time < 0) {
			return null;
		}

		final DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(context, AppInstHistory.class);
		
		AppInstHistory history = 
				new AppInstHistory(context);
		
		history.setTime(time);
		history.setPackageName(packageName);
		history.setPackageAction(action);
		
		if (!containsHistory(context, history)) {
			connectivity.insert(history);
		} else {
			Logger.warnning("Skip existed same hitory: %s", history);
		}
		
		return history;
	}
	
	public static Set<String> getPackagesHasHistories(Context context) {
		if (context == null) {
			return null;
		}
		
		final DatabaseReader<AppInstHistory> reader = 
				new DatabaseReader<AppInstHistory>(context, AppInstHistory.class);

		Query query = new Query(AppInstHistory.class);
		
		OrderingToken groupByToken = 
				AppInstHistory.COLUMN_PACKAGE_NAME.groupBy();
		if (groupByToken == null) {
			return null;
		}

		query.setGroupBy(groupByToken);
		
		List<AppInstHistory> histories = reader.query(query);
		if (histories == null || histories.size() <= 0) {
			return null;
		}
		
		Set<String> packages = new HashSet<String>();
		
		for (AppInstHistory aih: histories) {
			packages.add(aih.getPackageName());
		}
		
		return packages;
	}

	public static boolean containsHistory(Context context,
			AppInstHistory history) {
		if (history == null) {
			return false;
		}
		
		return containsHistory(context, history.getTime(),
				history.getPackageName(), history.getPackageAction());
	}
	
	public static boolean containsHistory(Context context,
			long time, String pkgName, String action) {
		if (context == null 
				|| pkgName == null
				|| action == null
				|| time < 0) {
			return false;
		}
		
		final DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(context, AppInstHistory.class);

		Query query = new Query(AppInstHistory.class);
		
		ExpressionToken selToken = 
				AppInstHistory.COLUMN_PACKAGE_NAME.eq(pkgName)
					.and(AppInstHistory.COLUMN_PACKAGE_ACTION.eq(action)
					.and(AppInstHistory.COLUMN_TIME.eq(time)));
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		List<DatabaseObject> histories = connectivity.query(query);
		
		return (histories != null && histories.size() > 0);
	}
	
	public static AppInstHistory getLatestHistory(Context context,
			AndroidApplication application) {
		if (context == null 
				|| application == null) {
			return null;
		}
		
		return getLatestHistory(context, application.getPackageName());
	}
	
	public static AppInstHistory getLatestHistory(Context context,
			String packageName) {
		if (context == null 
				|| packageName == null) {
			return null;
		}
		
		final DatabaseReader<AppInstHistory> reader = 
				new DatabaseReader<AppInstHistory>(context, AppInstHistory.class);

		Query query = new Query(AppInstHistory.class);
		
		ExpressionToken selToken =
				AppInstHistory.COLUMN_PACKAGE_NAME.eq(packageName);
		if (selToken == null) {
			return null;
		}
		
		OrderingToken orderByToken = 
				AppInstHistory.COLUMN_TIME.orderByDescending();
		if (orderByToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		query.setOrderBy(orderByToken);
		
		return reader.queryLastOne(query);
	}

	public static void clearHistory(Context context) {
		if (context == null) {
			return;
		}
		
		final DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(context, AppInstHistory.class);

		connectivity.delete(new Query(AppInstHistory.class));
	}

}
