package com.dailystudio.memory.boot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.lifestyle.GetupAndSleepAnalyzer;
import com.dailystudio.memory.lifestyle.TimeSpan;
import com.dailystudio.memory.lifestyle.TimeSpanGroup;
import com.dailystudio.memory.querypiece.MemoryKeyNoteExtra;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;
import com.dailystudio.memory.querypiece.MemoryPieceQueryService;

public class MemoryBootPieceQueryService extends MemoryPieceQueryService {

	private final static String SERVICE_NAME = "memory-boot-piece-query-service";
	
	public MemoryBootPieceQueryService() {
		super(SERVICE_NAME);
	}

	@Override
	protected long onQueryMemoryPiceceCount(Context context, String queryArgs) {
		if (context == null) {
			return 0l;
		}
		
		TimeCapsuleDatabaseReader<MemoryBoot> reader =
			new TimeCapsuleDatabaseReader<MemoryBoot>(context, MemoryBoot.class);

		Query query = new Query(MemoryBoot.class);
		
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
			long[] timespan = MemoryPieceKeyNotes.extractTimeSpan(queryArgs);
			
			fillGetupAndSleepDigests(context, timespan[0], timespan[1], digests);
			fillBootDigests(context, timespan[0], timespan[1], digests);
		}
		
		return digests;
	}

	private void fillGetupAndSleepDigests(Context context,
			long start, long end,
			List<MemoryPieceDigest> digests) {
		if (context == null || digests == null) {
			return;
		}
		
		GetupAndSleepAnalyzer analyzer = new GetupAndSleepAnalyzer();
		
		TimeSpanGroup result = analyzer.doAnalyze(context, start, end);
		if (result != null) {
			TimeSpan ts = result.getAverageSpan();
			Logger.debug("keynotes: [%s - %s], gas = [%s]",
					CalendarUtils.timeToReadableString(start),
					CalendarUtils.timeToReadableString(end),
					ts);

			MemoryPieceDigest digest = null;
			
			digest = createGetupDigest(context, ts, start);
			if (digest != null) {
				digests.add(digest);
			}

			if (!CalendarUtils.isCurrentDay(start)) {
				final int ghour = getLocalHourOfTime(ts.start);
				final int shour = getLocalHourOfTime(ts.end);
				Logger.debug("keynotes: ghour = [%d], shour = [%d]",
						ghour, shour);
				digest = createSleepDigest(context, ts, start, (shour < ghour));
				if (digest != null) {
					digests.add(digest);
				}
			}
		}
	}
	
	private int getLocalHourOfTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		
		String hstr = sdf.format(time);
		
		int hour = 0;
		try {
			hour = Integer.parseInt(hstr);
		} catch (NumberFormatException e) {
			Logger.warnning("could not parse hour for time: %d[%s], %s",
					time, CalendarUtils.timeToReadableString(time),
					e.toString());
			
			hour = 0;
		}
		
		return hour;
	}
	
	private MemoryPieceDigest createGetupDigest(Context context, TimeSpan span, long start) {
		if (context == null || span == null) {
			return null;
		}
		
		if (span.start <= 0) {
			return null;
		}
		
		MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_getup));
				
		digest.setTimestamp(CalendarUtils.setTimeOfDate(span.start, start));
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginBoot.class));
		
		return digest;
	}
	
	private MemoryPieceDigest createSleepDigest(Context context, TimeSpan span, 
			long start,
			boolean overTheDay) {
		if (context == null || span == null) {
			return null;
		}
		
		if (span.end <= 0) {
			return null;
		}
		
		MemoryKeyNoteExtra extra  = new MemoryKeyNoteExtra();
		extra.overTheDayEnd = overTheDay;
		
		MemoryPieceDigest digest = new MemoryPieceDigest(
				context.getString(R.string.key_note_sleep));
				
		digest.setTimestamp(CalendarUtils.setTimeOfDate(span.end, 
				(overTheDay ? start + CalendarUtils.DAY_IN_MILLIS : start)));
		digest.setPluginComponent(new ComponentName(
				context.getApplicationContext(),
				PluginBoot.class));
		digest.setExtraData(extra);
		
		return digest;
	}
	
	private void fillBootDigests(Context context,
			long start, long end,
			List<MemoryPieceDigest> digests) {
		if (context == null || digests == null) {
			return;
		}

		TimeCapsuleDatabaseReader<MemoryBoot> reader =
				new TimeCapsuleDatabaseReader<MemoryBoot>(context, MemoryBoot.class);
		
		List<MemoryBoot> boots = reader.query(start, end);
		if (boots == null) {
			return;
		}
		
		MemoryPieceDigest digest = null;
		for (MemoryBoot b: boots) {
			digest = new MemoryPieceDigest(String.format(
					context.getString(R.string.key_note_reboot_device),
					b.getBootSequence()));
					
			digest.setTimestamp(b.getTime());
			digest.setPluginComponent(new ComponentName(
					context.getApplicationContext(),
					PluginBoot.class));
			
			digests.add(digest);
		}
	}
	
	@Override
	protected List<MemoryPieceCard> onQueryMemoryPiceceCards(Context context,
			String queryArgs) {
		return null;
	}

}
