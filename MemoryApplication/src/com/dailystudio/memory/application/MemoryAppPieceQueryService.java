package com.dailystudio.memory.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageComponent;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;
import com.dailystudio.memory.querypiece.MemoryPieceQueryService;
import com.dailystudio.nativelib.application.AndroidApplication;

public class MemoryAppPieceQueryService extends MemoryPieceQueryService {

	private final static long APP_INST_DIGEST_CHECK_INTERVAL = (6 * CalendarUtils.HOUR_IN_MILLIS);

	private final static long APP_USAGE_DURATION_THRESHOLD = (30 * CalendarUtils.MINUTE_IN_MILLIS);
	
	private final static String SERVICE_NAME = "memory-app-piece-query-service";
	
	public MemoryAppPieceQueryService() {
		super(SERVICE_NAME);
	}

	@Override
	protected long onQueryMemoryPiceceCount(Context context, String queryArgs) {
		if (context == null) {
			return 0l;
		}

		TimeCapsuleDatabaseReader<Usage> reader =
			new TimeCapsuleDatabaseReader<Usage>(context, Usage.class);

		Query query = new Query(Usage.class);
		
		OrderingToken groupByToken = 
				Usage.COLUMN_COMPONENT_ID.groupBy();
		if (groupByToken != null) {
			query.setGroupBy(groupByToken);
		}
		
		final long count =  reader.queryCount(query);
		Logger.debug("count = %d", count);
		
		return count;
	}

	@Override
	protected List<MemoryPieceDigest> onQueryMemoryPiceceDigests(
			Context context, String queryArgs) {
		Logger.debug("queryArgs = %s", queryArgs);
		if (TextUtils.isEmpty(queryArgs)) {
			return null;
		}
		
		List<MemoryPieceDigest> digests = new ArrayList<MemoryPieceDigest>();

		if (queryArgs.startsWith(MemoryPieceKeyNotes.ARG_KEY_NOTES)) {
			final long[] timespan = MemoryPieceKeyNotes.extractTimeSpan(queryArgs);
			
			fillAppInstDigests(context, timespan, digests);
			fillAppUsagesDigests(context, timespan, digests);
		}
		
		return digests;	
	}
	
	private void fillAppUsagesDigests(Context context, long[] timespan,
			List<MemoryPieceDigest> digests) {
		if (context == null
				|| timespan == null || timespan.length < 2
				|| digests == null) {
			return;
		}

		TimeCapsuleDatabaseReader<Usage> reader =
				new TimeCapsuleDatabaseReader<Usage>(context, Usage.class);
		
		final Query query = getUsageQuery(timespan);
		if (query == null) {
			return;
		}
		
		List<Usage> usages = reader.query(query);
		if (usages == null) {
			return;
		}
		
		MemoryPieceDigest digest = null;
		UsageComponent comp = null;
		for (Usage u: usages) {
			comp = UsageComponentDatabaseModal.getComponent(
					context, u.getComponentId());
/*			Logger.debug("comp = %s, [usage: %s]", 
					comp, u);
*/			
			if (u.getDuration() >= APP_USAGE_DURATION_THRESHOLD) {
				digest = createAppUsageDigest(context, u, comp);
				if (digest != null) {
					digests.add(digest);
				}
			}
		}
	}
	
	private MemoryPieceDigest createAppUsageDigest(Context context, Usage u,
			UsageComponent comp) {
		if (context == null || u == null || comp == null) {
			return null;
		}
		
		AndroidApplication app = new AndroidApplication(comp.getPackageName());
		if (AndroidApplication.isDefaultLauncherApp(context, 
				app.getPackageName())) {
			return null;
		}
		
		app.resolveResources(context);
		
		MemoryPieceDigest digest = new MemoryPieceDigest(
				String.format(context.getString(
						R.string.key_note_apps_usage_brief), 
						app.getLabel(),
						APP_USAGE_DURATION_THRESHOLD / CalendarUtils.MINUTE_IN_MILLIS));
		
		digest.setTimestamp(u.getTime() + u.getDuration());
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginApp.class));
		
		return digest;
	}

	protected Query getUsageQuery(long[] timespan) {
		final long start = timespan[0];
		final long end = timespan[1];
		Logger.debug("peroid = [%s - %s]", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		Query query = null;
		
		if (end <= start) {
			return null;
		}
		
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(Usage.class);
			
		query = builer.getQueryForIntersect(
				Usage.COLUMN_TIME, 
				Usage.COLUMN_DURATION,
				start, end);
		
/*		OrderingToken groupBy = Usage.COLUMN_COMPONENT_ID.groupBy();
		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
*/		
		OrderingToken orderByToken = Usage.COLUMN_DURATION.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		ExpressionToken limitToken = new ExpressionToken(5);
		if (limitToken != null) {
			query.setLimit(limitToken);
		}

		return query;
	}

	
	private void fillAppInstDigests(Context context, long[] timespan,
			List<MemoryPieceDigest> digests) {
		if (context == null
				|| timespan == null || timespan.length < 2
				|| digests == null) {
			return;
		}
		
		TimeCapsuleDatabaseReader<AppInstHistory> reader =
				new TimeCapsuleDatabaseReader<AppInstHistory>(context,
						AppInstHistory.class);
		
		List<AppInstHistory> histories = reader.query(timespan[0], 
				timespan[1]);

		Map<Long, List<AppInstHistory>> map = 
				remapAppInstHistories(context, histories);
		if (map == null || map.size() <= 0) {
			return;
		}
		
		MemoryPieceDigest digest = null;
		String content = null;
		final Set<Long> hours = map.keySet();
		for (Long hour: hours) {
			content = composeDigestContent(context, map.get(hour));
			if (TextUtils.isEmpty(content)) {
				continue;
			}
			
			digest = new MemoryPieceDigest(content);
			
			digest.setTimestamp(hour + 30 * CalendarUtils.MINUTE_IN_MILLIS);
			digest.setPluginComponent(new ComponentName(
					context.getApplicationContext(),
					PluginApp.class));
			
			digests.add(digest);
		}
	}
	
	private String composeDigestContent(Context context,
			List<AppInstHistory> histories) {
		if (context == null 
				|| histories == null
				|| histories.size() <= 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		Set<String> pkgs = new HashSet<String>();
		String pkg = null;
		for (AppInstHistory h: histories) {
			pkg = h.getPackageName();
			
			if (pkgs.contains(pkg)) {
				continue;
			}
			
			pkgs.add(pkg);
		}
		
		Logger.debug("pkgs = %s", pkgs);
		AppInstHistory firstHistory = histories.get(0);
		firstHistory.resolveResources(context);
		
		if (pkgs.size() == 1) {
			final CharSequence action = AppInstHistory.getAciontLabel(context,
					firstHistory.getPackageAction());
			builder.append(String.format(context.getString(
					R.string.key_note_app_inst_brief), 
					firstHistory.getLabel(),
					action));
		} else {
			builder.append(String.format(context.getString(
					R.string.key_note_apps_inst_brief), 
					firstHistory.getLabel(),
					pkgs.size()));
		}
		return builder.toString();
	}

	private Map<Long, List<AppInstHistory>> remapAppInstHistories(
			Context context, List<AppInstHistory> histories) {
		if (context == null
				|| histories == null 
				|| histories.size() <= 0) {
			return null;
		}
		
		Map<Long, List<AppInstHistory>> hourMaps = 
				new HashMap<Long, List<AppInstHistory>>();
		
		long time = 0;
		long htime = 0;
		List<AppInstHistory> sublist = null;
		for (AppInstHistory h: histories) {
			htime = h.getTime();
			time = htime - (htime % APP_INST_DIGEST_CHECK_INTERVAL);
			if (CalendarUtils.getStartOfDay(time)
					!= CalendarUtils.getStartOfDay(htime)) {
				time = CalendarUtils.getStartOfDay(htime);
			}
			
			sublist = hourMaps.get(time);
			if (sublist == null) {
				sublist = new ArrayList<AppInstHistory>();
				
				hourMaps.put(time, sublist);
			}
			
			sublist.add(h);
		}
		
		return hourMaps;
	}

	@Override
	protected List<MemoryPieceCard> onQueryMemoryPiceceCards(Context context,
			String queryArgs) {
		return null;
	}

}
