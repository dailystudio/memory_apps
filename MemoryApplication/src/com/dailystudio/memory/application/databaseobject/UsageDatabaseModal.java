package com.dailystudio.memory.application.databaseobject;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;

public class UsageDatabaseModal {
	
	public static Usage getFirstUsage(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<Usage> reader =
			new TimeCapsuleDatabaseReader<Usage>(context, Usage.class);
		
		Query query = new Query(Usage.class);
		
		OrderingToken orderByToken = TimeCapsule.COLUMN_TIME.orderByAscending();

		query.setOrderBy(orderByToken);
		
		query.setLimit(new ExpressionToken(1));
	
		List<Usage> objects = reader.query(query);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		return objects.get(0);
	}
	
	public static Usage getLastUsage(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<Usage> reader =
			new TimeCapsuleDatabaseReader<Usage>(context, Usage.class);
		
		Query query = new Query(Usage.class);
		
		OrderingToken orderByToken = TimeCapsule.COLUMN_TIME.orderByDescending();

		query.setOrderBy(orderByToken);
		
		query.setLimit(new ExpressionToken(1));
	
		List<Usage> objects = reader.query(query);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		return objects.get(0);
	}

	public static void markActivityUsageStart(Context context, ComponentName component) {
		if (context == null || component == null) {
			return;
		}
		
		Usage lastUsage = getLastUsage(context);

		final int lastCompId = (lastUsage == null ? 
				Constants.INVALID_ID : lastUsage.getComponentId());
		int currCompId = getComponentId(context, component);

		final long lastBootSeq = (lastUsage == null ? 
				Constants.INVALID_ID : lastUsage.getBootSequence());
		final long currBootSeq = 
			MemoryBootDatabaseModal.getCurrentBootSeqeunce(context);

		if (lastUsage != null) {
			if (lastCompId == currCompId 
					&& lastBootSeq == currBootSeq
					&& lastUsage.getDuration() == 0) {
				Logger.debug("SKIP: SAME AS lastUsage = %s", lastUsage);
				return;
			}
			
			markActivityUsageEnd(context, lastUsage);
		}
		
		Usage usage = new Usage(context);
		
		final long now = System.currentTimeMillis();

		if (currCompId == Constants.INVALID_ID) {
			currCompId = UsageComponentDatabaseModal.addComponent(context,
					component);
			if (currCompId == Constants.INVALID_ID) {
				return;
			}
		}
		
		usage.setTime(now);
		usage.setBootSequence(currBootSeq);
		usage.setComponentId(currCompId);
		usage.setDuration(0);
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, Usage.class);
		
		connectivity.insert(usage);
	}

	public static void markCurrentActivityUsageEnd(Context context) {
		final Usage lastUsage = getLastUsage(context);
		if (lastUsage == null) {
			return;
		}
		
		markActivityUsageEnd(context, lastUsage);
	}
	
	public static void markActivityUsageEnd(Context context, Usage usage) {
		if (context == null || usage == null) {
			return;
		}
		
		Logger.debug("usage = %s", usage);
		if (usage.getDuration() > 0) {
			Logger.debug("SKIP: ALREADY FINISHED usage = %s", usage);
			return;
		}
		
		final long usageBootSeq = usage.getBootSequence();
		final long currBootSeq = 
			MemoryBootDatabaseModal.getCurrentBootSeqeunce(context);
		
		final long now = System.currentTimeMillis();

		long stopTime = 0;
		if (usageBootSeq != currBootSeq) {
			final MemoryBoot boot = 
				MemoryBootDatabaseModal.getBoot(context, usageBootSeq);
			if (boot != null) {
				stopTime = boot.getShutDownTime();
				if (stopTime <= 0) {
					stopTime = now;
				}
			}
		} else {
			stopTime = now;
		}
		
		long time = usage.getTime();

		long duration = stopTime - time;
		if (duration < 0) {
			duration = 0;
		}
		
		usage.setDuration(duration);
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, Usage.class);
		
		Query query = new Query(Usage.class);
		ExpressionToken selToken = TimeCapsule.COLUMN_ID.eq(usage.getId());
		query.setSelection(selToken);
			
		connectivity.update(query, usage);
	}
	
	private static int getComponentId (Context context, ComponentName componentName) {
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
	
}
