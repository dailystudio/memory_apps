package com.dailystudio.memory.where.lifestyle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.lifestyle.TimeSpan;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;

public class WorktimeAnalyzer {
	
	public static final int LATEST_MORNING_WORKTIME_END_HOUR = 13;
	public static final int EARLIES_MORNING_WORKTIME_END_HOUR = 11;
	public static final int LATEST_AFTERNOON_WORKTIME_START_HOUR = 14;
	public static final int EARLIES_AFTERNOON_WORKTIME_END_HOUR = 18;

	private static final long LUNCH_TIME_THRESHOLD = (20 * CalendarUtils.MINUTE_IN_MILLIS);
	private static final long LUNCH_TIME_SHRRINK = (2 * CalendarUtils.MINUTE_IN_MILLIS);
	
	private class WorkTimeSpan {
		
		TimeSpan amTimeSpan;
		TimeSpan pmTimeSpan;
		
	}

	public TimeSpanGroup[] doAnalyze(Context context, long start, long end) {
    	return doAnalyze(context, start, end, false);
    }
	
	public TimeSpanGroup[] doAnalyze(Context context, long start, long end,
			boolean allowEndBound) {
		List<TimeSpan> amWorktimeSpans = new ArrayList<TimeSpan>();
		List<TimeSpan> pmWorktimeSpans = new ArrayList<TimeSpan>();
		
		WorkTimeSpan worktimeSpans = null;
		for (long time = start; time < end; time += CalendarUtils.DAY_IN_MILLIS) {
			worktimeSpans = analyzeWorkTime(context, time, time + CalendarUtils.DAY_IN_MILLIS - 1,
					allowEndBound);
			if (worktimeSpans != null) {
				Logger.debug("am span: %s", worktimeSpans.amTimeSpan);
				if (worktimeSpans.amTimeSpan != null) {
					amWorktimeSpans.add(worktimeSpans.amTimeSpan);
				}
				
				Logger.debug("pm span: %s", worktimeSpans.pmTimeSpan);
				if (worktimeSpans.pmTimeSpan != null) {
					pmWorktimeSpans.add(worktimeSpans.pmTimeSpan);
				}
			}
		}
		
		TimeSpanGroup amGroup = new TimeSpanGroup(amWorktimeSpans);
		TimeSpanGroup pmGroup = new TimeSpanGroup(pmWorktimeSpans);
		
		return new TimeSpanGroup[] {
				amGroup,
				pmGroup
		};
	}
	
    private WorkTimeSpan analyzeWorkTime(Context context, long start, long end, 
    		boolean allowNullBound) {
		TimeCapsuleDatabaseReader<IdspotHistory> reader =
				new TimeCapsuleDatabaseReader<IdspotHistory>(context,
						IdspotHistory.class);
		Query query = getWorkTimeQuery(context, start, end);
		if (query == null) {
			return null;
		}
		
		List<IdspotHistory> histories = reader.query(query);
		if (histories == null) {
			return null;
		}
		
		for (IdspotHistory h: histories) {
			Logger.debug("h: %s", h);
		}
		
		final IdspotHistory amStartHistory = getStartOfWorkAm(histories);
		final IdspotHistory amEndHistory = getEndOfWorkAm(histories);
		final IdspotHistory pmStartHistory = getStartOfWorkPm(histories);
		final IdspotHistory pmEndHistory = getEndOfWorkPm(histories);
		Logger.debug("amStartHistory = %s", amStartHistory);
		Logger.debug("amEndHistory = %s", amEndHistory);
		Logger.debug("pmStartHistory = %s", amStartHistory);
		Logger.debug("pmEndHistory = %s", pmEndHistory);
		
		WorkTimeSpan wtspan = new WorkTimeSpan();
		
		wtspan.amTimeSpan = createTimeSpan(amStartHistory, amEndHistory, 
				start, 
				start + (13 * CalendarUtils.HOUR_IN_MILLIS),
				allowNullBound);
		wtspan.pmTimeSpan = createTimeSpan(pmStartHistory, pmEndHistory, 
				start + (13 * CalendarUtils.HOUR_IN_MILLIS), 
				end,
				allowNullBound);

		return wtspan;
    }
    
    private TimeSpan createTimeSpan(IdspotHistory startHistory,
    		IdspotHistory endHistory, long start, long end,
    		boolean copyBoundToNull) {
		if (!copyBoundToNull 
				&& (startHistory == null || endHistory == null)) {
			return null;
		} else if (copyBoundToNull
				&& (startHistory == null && endHistory == null)) {
			return null;
		}
		
		boolean copiedStart = false; 
		boolean copiedEnd = false; 
		
		if (startHistory == null) {
			startHistory = endHistory;
			
			copiedStart = true; 
		}
		
		if (endHistory == null) {
			endHistory = startHistory;
			
			copiedEnd = true; 
		}
		
		final long tsStart = (copiedStart ? endHistory.getTime()
				: startHistory.getTime());
		long tsEnd = (copiedEnd ? startHistory.getTime() 
				: (endHistory.getTime() + endHistory.getDuration()));
		if (tsEnd > end) {
			tsEnd = end;
		}
		
		return new TimeSpan(tsStart, tsEnd);
    }
    
	private IdspotHistory getStartOfWorkAm(List<IdspotHistory> histories) {
		if (histories == null || histories.size() <= 0) {
			return null;
		}
		
		return histories.get(0);
	}
    
	private IdspotHistory getEndOfWorkAm(List<IdspotHistory> histories) {
		return guessLunchBreak(histories, true);
	}

	private IdspotHistory getStartOfWorkPm(List<IdspotHistory> histories) {
		return guessLunchBreak(histories, false);
	}
	
	private IdspotHistory guessLunchBreak(List<IdspotHistory> histories,
			boolean wantStartHistory) {
		if (histories == null) {
			return null;
		}

		final int N = histories.size();
		if (N <= 0) {
			return null;
		}
		
		if (N < 2) {
			IdspotHistory h = histories.get(0);
			if (wantStartHistory) { 
				if (CalendarUtils.getHour(h.getTime()) < LATEST_MORNING_WORKTIME_END_HOUR) {
					return h;
				} else {
					return null;
				}
			} else {
				if ((CalendarUtils.getHour(h.getTime()) >= LATEST_MORNING_WORKTIME_END_HOUR
						|| CalendarUtils.getHour(h.getTime() + h.getDuration()) >= LATEST_MORNING_WORKTIME_END_HOUR)) {
					return h;
				}	else {
					return null;
				}
			}
		}
		
		IdspotHistory h1 = null;
		IdspotHistory h2 = null;
		for (int offset = 1; offset >= 0; offset--) {
			for (long timeshrink = 0; timeshrink <= (LUNCH_TIME_SHRRINK * 3); timeshrink += LUNCH_TIME_SHRRINK) {
				for (int i = (N - 1); i >= 1; i--) {
					h1 = histories.get(i);
					h2 = histories.get(i - 1);
					if (likeALunchBreak(h1, h2, LUNCH_TIME_THRESHOLD - timeshrink, 
							EARLIES_MORNING_WORKTIME_END_HOUR - offset,
							LATEST_MORNING_WORKTIME_END_HOUR - offset, 
							LATEST_AFTERNOON_WORKTIME_START_HOUR - offset)) {
						return (wantStartHistory ? h2 : h1);
					}
				}
			}
		}
		
		return null;
	}
	
	private boolean likeALunchBreak(IdspotHistory h1, IdspotHistory h2,
			long lunchThreshold,
			long earliest_am_end_hour, 
			long latest_am_end_hour,
			long latest_pm_start_hour) {
/*		Logger.debug("h1 = [%s], h2 = [%s - %s], param[threshold: %s(%s), eae: %d(%s), lae:% d(%s), lps: %d(%s)", 
				CalendarUtils.timeToReadableString(h1.getTime()),
				CalendarUtils.timeToReadableString(h2.getTime()),
				CalendarUtils.timeToReadableString(h2.getTime() + h2.getDuration()), 
				CalendarUtils.durationToReadableString(lunchThreshold),
				CalendarUtils.durationToReadableString(h1.getTime() - (h2.getTime() + h2.getDuration())),
				earliest_am_end_hour,
				(CalendarUtils.getHour(h2.getTime() + h2.getDuration()) >= earliest_am_end_hour),
				latest_am_end_hour,
				(CalendarUtils.getHour(h2.getTime()) < latest_am_end_hour),
				latest_pm_start_hour,
				CalendarUtils.getHour(h1.getTime()) <= latest_pm_start_hour);
*/		
		if ((h1.getTime() - (h2.getTime() + h2.getDuration())) >= lunchThreshold) {
			if (CalendarUtils.getHour(h2.getTime()) < latest_am_end_hour
					&& CalendarUtils.getHour(h2.getTime() + h2.getDuration()) >= earliest_am_end_hour
					&& CalendarUtils.getHour(h1.getTime()) <= latest_pm_start_hour) {
				return true;
			}
		}
		
		return false;
	}

	private IdspotHistory getEndOfWorkPm(List<IdspotHistory> histories) {
		if (histories == null || histories.size() <= 0) {
			return null;
		}
		
		IdspotHistory history = histories.get(histories.size() - 1);
		if (history == null || history.getDuration() <= 0) {
			return null;
		}
		
		return history;
	}
    
   private Query getWorkTimeQuery(Context context, long start, long end) {
    	if (context == null ||
    			end <= start) {
    		return null;
    	}
    	
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(IdspotHistory.class);
		
		Query query = builer.getQueryForIntersect(
				IdspotHistory.COLUMN_TIME, 
				IdspotHistory.COLUMN_DURATION,
				start, end);	
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
				query.getSelection();
		
		ExpressionToken identitySelection = 
				IdspotHistory.COLUMN_IDENTITY.eq(
						String.valueOf(HotspotIdentity.HOTSPOT_WORKPLACE));
		if (selToken == null) {
			selToken = identitySelection;
		} else {
			selToken = selToken.and(identitySelection);
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		OrderingToken orderByToken =
				IdspotHistory.COLUMN_TIME.orderByAscending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
    }

}
