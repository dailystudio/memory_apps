package com.dailystudio.memory;

import java.util.List;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.database.MemoryKeyNoteCacheDatabaseModal;
import com.dailystudio.memory.querypiece.MemoryKeyNote;
import com.dailystudio.memory.querypieces.AllKeynotes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class MemoryKeynoteCacheService extends IntentService {

	private final static String SERVICE_NAME = "keynote-cache-service";
	
	public final static String ACTION_UPDATE_KEYNOTES_CACHES = 
			"memory.intent.ACTION_UPDATE_KEYNOTES_CACHES";
	
	public final static String ACTION_KEYNOTES_CACHES_UPDATED = 
			"memory.intent.ACTION_KEYNOTES_CACHES_UPDATED";
	
	public MemoryKeynoteCacheService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Logger.debug("intent = %s", intent);
		if (intent == null) {
			return;
		}
		
		final String action = intent.getAction();
		if (ACTION_UPDATE_KEYNOTES_CACHES.equals(action)) {
			final long start = intent.getLongExtra(Constants.EXTRA_PEROID_START, -1);
			final long end = intent.getLongExtra(Constants.EXTRA_PEROID_END, -1);
			
			updateKeynoteCaches(start, end);
		}
	}

	private void updateKeynoteCaches(long start, long end) {
		Logger.debug("update keynote caches for: [%d(%s) - %d(%s)]",
				start, CalendarUtils.timeToReadableString(start),
				end, CalendarUtils.timeToReadableString(end));
		if (start < 0 || end < 0 || start >= end) {
			return;
		}
		
		final Context context = getApplicationContext();
		
		AllKeynotes allKeynotes = new AllKeynotes(start, end);
		
		List<MemoryKeyNote> keynotes = allKeynotes.queryKeynotes(context);
		
		MemoryKeyNoteCacheDatabaseModal.clearKeyNoteCache(context, start, end);
		MemoryKeyNoteCacheDatabaseModal.cacheKeyNotes(context, keynotes,
				start, end);
		
		notifyCachesUpdated(start, end);
	}
	
	private void notifyCachesUpdated(long start, long end) {
		Intent notifyIntent = new Intent(ACTION_KEYNOTES_CACHES_UPDATED);
		
		notifyIntent.putExtra(Constants.EXTRA_PEROID_START, start);
		notifyIntent.putExtra(Constants.EXTRA_PEROID_END, end);
		
		sendBroadcast(notifyIntent);
	}

}
