package com.dailystudio.memory.where;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;
import com.dailystudio.memory.querypiece.MemoryPieceQueryService;
import com.dailystudio.memory.where.card.Cards;
import com.dailystudio.memory.where.card.WhereCardsUpdater;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.lifestyle.WorktimeAnalyzer;

public class MemoryWherePieceQueryService extends MemoryPieceQueryService {
	
	private static final String ARG_NOWHERE = "now-where";

	private final static String SERVICE_NAME = "memory-where-piece-query-servce";
	
	public MemoryWherePieceQueryService() {
		super(SERVICE_NAME);
	}

	@Override
	protected long onQueryMemoryPiceceCount(Context context, String queryArgs) {
		return 0l;
	}

	@Override
	protected List<MemoryPieceDigest> onQueryMemoryPiceceDigests(
			Context context, String queryArgs) {
		Logger.debug("queryArgs = %s", queryArgs);
		if (TextUtils.isEmpty(queryArgs)) {
			return null;
		}
		
		List<MemoryPieceDigest> digests = new ArrayList<MemoryPieceDigest>();

		MemoryPieceDigest digest = null;
		if (ARG_NOWHERE.equals(queryArgs)) {
			IdspotHistory history = 
					IdspotHistoryDatabaseModal.getLastHistory(context);
			Logger.debug("history = %s", history);
			if (history != null && history.getDuration() <= 0) {
				HotspotIdentity identity = history.getIdentity();
				Logger.debug("identity = %s", identity);

				if (identity != null) {
					digest = new MemoryPieceDigest(String.valueOf(identity));
					
					digest.setTimestamp(history.getTime());
					digest.setPluginComponent(new ComponentName(
							context.getApplicationContext(),
							PluginWhere.class));
					
					digests.add(digest);
				}
			}
		} else if (queryArgs.startsWith(MemoryPieceKeyNotes.ARG_KEY_NOTES)) {
			final long[] timespan = MemoryPieceKeyNotes.extractTimeSpan(queryArgs);
			
			TimeSpanGroup[] results = new WorktimeAnalyzer().doAnalyze(
					context, timespan[0], timespan[1], true);
			Logger.debug("keynote: results = [%s]", 
					ArrayUtils.arrayToString(results));
			
			final boolean isToday = CalendarUtils.isCurrentDay(timespan[0]);

			fillKeyNotesDigests(context, timespan, results, digests, isToday);
		}
		
		return digests;
	}
	
	private void fillKeyNotesDigests(Context context, 
			long[] timespans,
			TimeSpanGroup[] results,
			List<MemoryPieceDigest> digests, 
			boolean today) {
		if (context == null
				|| timespans == null || timespans.length < 2 
				|| results == null || results.length < 2 
				|| digests == null) {
			return;
		}
		
		final long now = System.currentTimeMillis();
		
		MemoryPieceDigest digest = null;
		
		digest = buildArriveOfficeAmDigest(context, results);
		if (isValidDigest(digest)) {
			clampTimestamp(digest, timespans[0], false);
			digests.add(digest);
		}
		
		if (!today || CalendarUtils.getHour(now) >= WorktimeAnalyzer.LATEST_MORNING_WORKTIME_END_HOUR) {
			digest = buildLunchTimeDigest(context, results);
			if (isValidDigest(digest)) {
				clampTimestamp(digest, timespans[0], false);
				digests.add(digest);
			}
		}
		
		if (!today || CalendarUtils.getHour(now) >= WorktimeAnalyzer.LATEST_AFTERNOON_WORKTIME_START_HOUR) {
			digest = buildArriveOfficePmDigest(context, results);
			if (isValidDigest(digest)) {
				clampTimestamp(digest, timespans[0], false);
				digests.add(digest);
			}
		}
		
		if (!today || CalendarUtils.getHour(now) >= WorktimeAnalyzer.EARLIES_AFTERNOON_WORKTIME_END_HOUR) {
			digest = buildReturnHomeDigest(context, results);
			if (isValidDigest(digest)) {
				clampTimestamp(digest, timespans[0], false);
				digests.add(digest);
			}
		}
	}
	
	private void clampTimestamp(MemoryPieceDigest digest, long targetDateInMillis, 
			boolean overTheDay) {
		if (digest == null) {
			return;
		}
		
		final long oldStamp = digest.getTimestamp();
		
		digest.setTimestamp(CalendarUtils.setTimeOfDate(oldStamp, 
				(overTheDay ? targetDateInMillis + CalendarUtils.DAY_IN_MILLIS 
						: targetDateInMillis)));
	}
	
	private MemoryPieceDigest buildArriveOfficeAmDigest(Context context,
			TimeSpanGroup[] results) {
		Logger.debug("keynote: worktime.am = [%s]", results[0]);

		final long wamstartavg = results[0].getStartAverage();
		if (wamstartavg <= 0) {
			return null;
		}
	
		final MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_arrive_office));
				
		digest.setTimestamp(wamstartavg);
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginWhere.class));
	
		return digest;
	}
	
	private MemoryPieceDigest buildArriveOfficePmDigest(Context context,
			TimeSpanGroup[] results) {
		final long wpmstart = results[1].getStartAverage();
		if (CalendarUtils.getHour(wpmstart) > WorktimeAnalyzer.LATEST_AFTERNOON_WORKTIME_START_HOUR) {
			return null;
		}

		Logger.debug("keynote: worktime.pm = [%s]", results[1]);
		
		final MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_back_to_office));
				
		digest.setTimestamp(wpmstart);
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginWhere.class));
	
		return digest;
	}

	private MemoryPieceDigest buildReturnHomeDigest(Context context,
			TimeSpanGroup[] results) {
		final long wpmstart = results[1].getStartAverage();
		final long wpmendavg = results[1].getEndAverage();
		if (wpmendavg <= wpmstart) {
			return null;
		}

		Logger.debug("keynote: worktime.pm = [%s]", results[1]);
		
		final MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_return_home));
				
		digest.setTimestamp(wpmendavg);
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginWhere.class));
	
		return digest;
	}

	private MemoryPieceDigest buildLunchTimeDigest(Context context,
			TimeSpanGroup[] results) {
		final long wamendavg = results[0].getEndAverage();
		if (CalendarUtils.getHour(wamendavg) < WorktimeAnalyzer.EARLIES_MORNING_WORKTIME_END_HOUR) {
			return null;
		}
		
		Logger.debug("keynote: worktime.am = [%s]", results[0]);
		
		final MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_lunch_time));
				
		digest.setTimestamp(wamendavg);
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginWhere.class));
	
		return digest;
	}
	
	private boolean isValidDigest(MemoryPieceDigest digest) {
		return (digest != null && digest.getTimestamp() > 0);
	}

	@Override
	protected List<MemoryPieceCard> onQueryMemoryPiceceCards(Context context,
			String queryArgs) {
		asyncUpdateCards(context);
		
		List<MemoryPieceCard> cards = new ArrayList<MemoryPieceCard>();

		MemoryPieceCard card = null;
		
		card = new MemoryPieceCard();
		card.setCardTitle(context.getString(R.string.dashboard_my_places));
		card.setCardUri(Cards.CARD_MY_PLACES_PIE_CHART_FILE);
		card.setTimestamp(System.currentTimeMillis());
		card.setPluginComponent(new ComponentName(
						context.getApplicationContext(),
						PluginWhere.class));
		cards.add(card);
		
		IdspotHistory history = 
				IdspotHistoryDatabaseModal.getLastHistory(context);
		Logger.debug("history = %s", history);
		if (history != null && history.getDuration() <= 0) {
			card = new MemoryPieceCard();
			card.setCardTitle(context.getString(
					R.string.dashboard_idpost_now));
			card.setCardUri(Cards.CARD_IDSPOT_NOW_FILE);
			card.setTimestamp(System.currentTimeMillis());
			card.setPluginComponent(new ComponentName(
							context.getApplicationContext(),
							PluginWhere.class));
			cards.add(card);
		}
		
		return cards;
	}

	private void asyncUpdateCards(final Context context) {
		if (context == null) {
			return;
		}
		
		Logger.debug("build card: %s", Cards.CARD_MY_PLACES_PIE_CHART);
		new WhereCardsUpdater(context, 
				Cards.CARD_MY_PLACES_PIE_CHART).doUpdate();
		Logger.debug("build card: %s", Cards.CARD_IDSPOT_NOW);
		new WhereCardsUpdater(context, 
				Cards.CARD_IDSPOT_NOW).doUpdate();
	}
	
}
