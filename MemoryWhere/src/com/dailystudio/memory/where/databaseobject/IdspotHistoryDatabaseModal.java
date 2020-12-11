package com.dailystudio.memory.where.databaseobject;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.boot.MemoryBootDatabaseModal;

public class IdspotHistoryDatabaseModal {

	public static IdspotHistorySummary getSummaryForIdspot(Context context,
			IdentifiedHotspot idspot, long start, long end) {
		Logger.debug("idspot = %s", idspot);
		if (context == null || idspot == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<IdspotHistory> reader = 
			new TimeCapsuleDatabaseReader<IdspotHistory>(context, IdspotHistory.class);
		
		Query query = null;
		if (end <= start) {
			query = new Query(IdspotHistory.class);
		} else {
			TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(IdspotHistory.class);
			
			query = builer.getQueryForIntersect(
					IdspotHistory.COLUMN_TIME, 
					IdspotHistory.COLUMN_DURATION,
					start, end);
		}
		
		ExpressionToken idspotSelToken = 
				IdspotHistory.COLUMN_IDSPOT_ID.eq(idspot.getId());
		if (idspotSelToken == null) {
			return null;
		}
		
		ExpressionToken selToken = query.getSelection();
		if (selToken == null) {
			selToken = idspotSelToken;
		} else {
			selToken.and(idspotSelToken);
		}
						
		OrderingToken groupByToken = 
				IdspotHistory.COLUMN_IDSPOT_ID.groupBy();
		if (groupByToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		query.setGroupBy(groupByToken);
		
		List<DatabaseObject> objects = 
				reader.query(query, IdspotHistorySummary.class);
		Logger.debug("objects = %s", objects);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		DatabaseObject object0 = objects.get(0);
		if (object0 instanceof IdspotHistorySummary == false) {
			return null;
		}
		
		return (IdspotHistorySummary)object0;
	}
	
	public static boolean isLastHistory(Context context, 
			IdspotHistory history) {
		if (context == null || history == null) {
			return false;
		}
		
		final IdspotHistory last = getLastHistory(context);
		if (last == null) {
			return false;
		}
		
		return (history.getId() == last.getId());
	}

	public static boolean isSummaryOfLastHistory(Context context, 
			IdspotHistorySummary summary) {
		if (context == null || summary == null) {
			return false;
		}
		
		final IdspotHistory last = getLastHistory(context);
		if (last == null) {
			return false;
		}
		
		return (summary.getIdentifiedHotspotId() == last.getId());
	}

	public static IdspotHistory getLastHistory(Context context) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<IdspotHistory> reader = 
			new TimeCapsuleDatabaseReader<IdspotHistory>(context, IdspotHistory.class);

		return reader.queryLastOne();
	}

	public static IdspotHistory getFirstHistory(Context context) {
		if (context == null) {
			return null;
		}
		
		final DatabaseReader<IdspotHistory> reader = 
			new DatabaseReader<IdspotHistory>(context, IdspotHistory.class);
		
		Query query = new Query(IdspotHistory.class);
		
		OrderingToken orderByToken = TimeCapsule.COLUMN_TIME.orderByAscending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return reader.queryLastOne(query);
	}

	public static boolean markEnterIdspot(Context context, IdentifiedHotspot idspot) {
		Logger.debug("mark entering of idspot: %s", idspot);
		if (context == null || idspot == null) {
			return false;
		}
		
		IdspotHistory lastIdspotHistory = getLastHistory(context);

		final long lastBootSeq = (lastIdspotHistory == null ? 
				Constants.INVALID_ID : lastIdspotHistory.getBootSequence());
		final long currBootSeq = 
			MemoryBootDatabaseModal.getCurrentBootSeqeunce(context);

		if (lastIdspotHistory != null) {
			if (lastBootSeq == currBootSeq
					&& lastIdspotHistory.isNearBy(idspot)
					&& lastIdspotHistory.getDuration() == 0) {
				Logger.debug("SKIP: SAME AS lastIdspotHistory = %s", lastIdspotHistory);
				return false;
			}
			
			markLeaveIdspot(context, lastIdspotHistory);
		}
		
		IdspotHistory history = new IdspotHistory(context);
		
		final long now = System.currentTimeMillis();

		history.setIdentity(idspot.getIdentity());
		history.setIdentifiedHotspotId(idspot.getId());
		history.setLatitude(idspot.getLatitude());
		history.setLongitude(idspot.getLongitude());
		history.setAltitude(idspot.getAltitude());
		history.setTime(now);
		history.setBootSequence(currBootSeq);
		history.setDuration(0);
		
		final DatabaseConnectivity connectivity = 
			new DatabaseConnectivity(context, IdspotHistory.class);
		
		connectivity.insert(history);
		
		return true;
	}

	public static void markLeaveIdspot(Context context) {
		final IdspotHistory lastIdspotHistory = getLastHistory(context);
		if (lastIdspotHistory == null) {
			return;
		}
		
		markLeaveIdspot(context, lastIdspotHistory);
	}
	
	public static void markLeaveIdspot(Context context, IdspotHistory idspotHistory) {
		Logger.debug("mark leaving of idspot: %s", idspotHistory);
		if (context == null || idspotHistory == null) {
			return;
		}
		
		Logger.debug("history = %s", idspotHistory);
		if (idspotHistory.getDuration() > 0) {
			Logger.debug("SKIP: ALREADY LEAVED lastIdspotHistory = %s", idspotHistory);
			return;
		}
		
		final long usageBootSeq = idspotHistory.getBootSequence();
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
		
		long time = idspotHistory.getTime();

		long duration = stopTime - time;
		if (duration < 0) {
			duration = 0;
		}
		
		idspotHistory.setDuration(duration);
		
		final TimeCapsuleDatabaseWriter<IdspotHistory> writer = 
			new TimeCapsuleDatabaseWriter<IdspotHistory>(context,
					IdspotHistory.class);
		
		writer.update(idspotHistory);
	}
	
}
