package com.dailystudio.memory.application.databaseobject;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.nativelib.application.AndroidApplication;

public class UselessAppDatabaseModal {

	public static UselessApp addUselessApp(Context context, long time,
			String packageName, boolean notUsedRecently, boolean notUpdatedRecently,
			long recentUsedTime, long recentUpdatedTime) {
		if (context == null 
				|| packageName == null) {
			return null;
		}
		
		if (time < 0) {
			return null;
		}

		final TimeCapsuleDatabaseWriter<UselessApp> writer =
				new TimeCapsuleDatabaseWriter<UselessApp>(context, UselessApp.class);
		
		UselessApp uapp = getUselessApp(context, packageName);
		if (uapp == null) {
			uapp = new UselessApp(context);
		}
		
		uapp.setTime(time);
		uapp.setPackageName(packageName);
		uapp.setNotUsedRecently(notUsedRecently);
		uapp.setNotUpdatedRecently(notUpdatedRecently);
		uapp.setRecentUsedTime(recentUsedTime);
		uapp.setRecentUpdatedTime(recentUpdatedTime);
		
		if (uapp.getId() <= 0) {
			writer.insert(uapp);
		} else {
			writer.update(uapp);
		}
		
		return uapp;
	}

	public static void removeUselessApp(Context context, String packageName) {
		if (context == null 
				|| packageName == null) {
			return;
		}
		
		final TimeCapsuleDatabaseWriter<UselessApp> writer =
				new TimeCapsuleDatabaseWriter<UselessApp>(context, UselessApp.class);
		
		UselessApp uapp = getUselessApp(context, packageName);
		if (uapp == null) {
			return;
		}

		writer.delete(uapp);
	}
	
	public static boolean isUselessApp(Context context, String pkgName) {
		if (context == null 
				|| pkgName == null) {
			return false;
		}
		
		final DatabaseReader<UselessApp> reader = 
				new DatabaseReader<UselessApp>(context, UselessApp.class);

		Query query = new Query(AppInstHistory.class);
		
		ExpressionToken selToken = 
				AppInstHistory.COLUMN_PACKAGE_NAME.eq(pkgName);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		UselessApp uapp = reader.queryLastOne(query);
		
		return (uapp != null);
	}
	
	public static UselessApp getLatestHistory(Context context,
			AndroidApplication application) {
		if (context == null 
				|| application == null) {
			return null;
		}
		
		return getUselessApp(context, application.getPackageName());
	}
	
	public static UselessApp getUselessApp(Context context,
			String packageName) {
		if (context == null 
				|| packageName == null) {
			return null;
		}
		
		final DatabaseReader<UselessApp> reader = 
				new DatabaseReader<UselessApp>(context, UselessApp.class);

		Query query = new Query(UselessApp.class);
		
		ExpressionToken selToken =
				UselessApp.COLUMN_PACKAGE_NAME.eq(packageName);
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

}
