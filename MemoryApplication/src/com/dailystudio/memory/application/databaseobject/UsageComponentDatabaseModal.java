package com.dailystudio.memory.application.databaseobject;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.AndroidComponentObject;

public class UsageComponentDatabaseModal {

	public static List<UsageComponent> listUsageComponents(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<UsageComponent> reader =
				new TimeCapsuleDatabaseReader<UsageComponent>(context,
						UsageComponent.class);
			
		Query query = new Query(UsageComponent.class);
		
		return reader.query(query);
	}
	
	public static String[] listUsagePackages(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<UsageComponent> reader =
				new TimeCapsuleDatabaseReader<UsageComponent>(context,
						UsageComponent.class);
			
		Query query = new Query(UsageComponent.class);
		
		OrderingToken groupByToken = 
				UsageComponent.COLUMN_PACKAGE_NAME.groupBy();
		if (groupByToken == null) {
			return null;
		}
		
		query.setGroupBy(groupByToken);
		
		List<UsageComponent> components = reader.query(query);
		if (components == null) {
			return null;
		}
		
		final int N = components.size();
		if (N <= 0) {
			return null;
		}
		
		List<String> packages = new ArrayList<String>(N);
		
		for (int i = 0; i < N; i++) {
			packages.add(components.get(i).getPackageName());
		}
			
		
		return packages.toArray(new String[0]);			
	}
	
	public static Integer[] getSystemComponentIds(Context context) {
		if (context == null) {
			return null;
		}
	
		final String[] packages = listUsagePackages(context);
		if (packages == null) {
			return null;
		}
		
		List<Integer> ids = new ArrayList<Integer>();
		
		boolean isSysApp = false;
		boolean isExisted = false;
		int[] compIds = null;
		for (String pkg: packages) {
			isExisted = AndroidApplication.isInstalled(context, pkg);
			if (isExisted == false) {
				continue;
			}		
			
			isSysApp = AndroidApplication.isSystemApplication(context, pkg);
//			Logger.debug("pkg = %s (sys: %s)", pkg, isSysApp);
			if (isExisted == false || isSysApp == false) {
				continue;
			}
			
			compIds = getComponentIds(context, pkg);
//			Logger.debug("pkg = %s (compIds: %s)", pkg, compIds);
			if (compIds == null || compIds.length <= 0) {
				continue;
			}
			
			for (int id: compIds) {
				ids.add(id);
			}
		}
		
		final int N = ids.size();
		if (N <= 0) {
			return null;
		}
		
		return ids.toArray(new Integer[0]);
	}
	
	public static Integer[] getUserComponentIds(Context context) {
		if (context == null) {
			return null;
		}
	
		final String[] packages = listUsagePackages(context);
		if (packages == null) {
			return null;
		}
		
		List<Integer> ids = new ArrayList<Integer>();
		
		boolean isSysApp = false;
		boolean isExisted = false;
		int[] compIds = null;
		for (String pkg: packages) {
			isExisted = AndroidApplication.isInstalled(context, pkg);
			if (isExisted == false) {
				continue;
			}		
			
			isSysApp = AndroidApplication.isSystemApplication(context, pkg);
//			Logger.debug("pkg = %s (sys: %s)", pkg, isSysApp);
			if (isSysApp) {
				continue;
			}
			
			compIds = getComponentIds(context, pkg);
//			Logger.debug("pkg = %s (compIds: %s)", pkg, compIds);
			if (compIds == null || compIds.length <= 0) {
				continue;
			}
			
			for (int id: compIds) {
				ids.add(id);
			}
		}
		
		final int N = ids.size();
		if (N <= 0) {
			return null;
		}
		
		return ids.toArray(new Integer[0]);
	}
	
	public static int[] getComponentIds(Context context, String packageName) {
		if (packageName == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<UsageComponent> reader =
			new TimeCapsuleDatabaseReader<UsageComponent>(context,
					UsageComponent.class);
		
		Query query = new Query(UsageComponent.class);
		ExpressionToken selToken = 
			UsageComponent.COLUMN_PACKAGE_NAME.eq(packageName);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		List<UsageComponent> components = reader.query(query);
		if (components == null) {
			return null;
		}
		
		final int N = components.size();
		if (N <= 0) {
			return null;
		}
		
		int[] compIds = new int[N];
		for (int i = 0; i < N; i++) {
			compIds[i] = components.get(i).getId();
		}
		
		return compIds;
	}

	public static int getComponentId (Context context, AndroidComponentObject component) {
		if (component == null) {
			return Constants.INVALID_ID;
		}
		
		return getComponentId(context, component.getComponentName());
	}
	
	public static int getComponentId (Context context, ComponentName componentName) {
		if (componentName == null) {
			return Constants.INVALID_ID;
		}
		
		final TimeCapsuleDatabaseReader<UsageComponent> reader =
			new TimeCapsuleDatabaseReader<UsageComponent>(context,
					UsageComponent.class);
		
		final String packageName = componentName.getPackageName();
		final String className = componentName.getClassName();
		if (packageName == null || className == null) {
			return Constants.INVALID_ID;
		}	
		
		Query query = new Query(UsageComponent.class);
		ExpressionToken selToken = 
			UsageComponent.COLUMN_PACKAGE_NAME.eq(packageName)
				.and(UsageComponent.COLUMN_CLASS_NAME.eq(className));
		query.setSelection(selToken);
		
		List<UsageComponent> results = reader.query(query);
		if (results == null || results.size() <= 0) {
			return Constants.INVALID_ID;
		}
		
		return results.get(0).getId();
	}
	
	public static int addComponent(Context context, ComponentName component) {
		if (component == null) {
			return Constants.INVALID_ID;
		}
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, UsageComponent.class);

		long now = System.currentTimeMillis();
		
		final String packageName = component.getPackageName();
		final String className = component.getClassName();
		if (packageName == null || className == null) {
			return Constants.INVALID_ID;
		}	
		
		UsageComponent andcomp = new UsageComponent(context);
		
		andcomp.setTime(now);
		andcomp.setPackageName(packageName);
		andcomp.setClassName(className);
		
		long rowid = connectivity.insert(andcomp);
		if (rowid > 0) {
			andcomp.setId((int)rowid);
		}
		Logger.debug("andcomp(%s)", andcomp);
		
		return andcomp.getId();
	}
	
	public static UsageComponent getComponent(Context context, int compId) {
		if (context == null || compId == Constants.INVALID_ID) {
			return null;
		}
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, UsageComponent.class);
		
		Query query = new Query(UsageComponent.class);

		ExpressionToken selToken = UsageComponent.COLUMN_ID.eq(compId);
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		List<DatabaseObject> comps = connectivity.query(query);
		if (comps == null || comps.size() <= 0) {
			return null;
		}
		
		return (UsageComponent)comps.get(0);
	}

}
