package com.dailystudio.memory.database;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.memory.querypiece.MemoryKeyNote;

import android.content.Context;

public class MemoryKeyNoteCacheDatabaseModal {
	
	public static void clearKeyNoteCache(Context context, 
			long start, long end) {
		if (context == null) {
			return;
		}
		
		if (start >= end) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<MemoryKeyNoteCache> writer =
				new TimeCapsuleDatabaseWriter<MemoryKeyNoteCache>(context,
						MemoryKeyNoteCache.class);
		
		Query query = new Query(MemoryKeyNoteCache.class);
		if (start < end) {
			ExpressionToken selection = MemoryKeyNoteCache.COLUMN_DAY_START.eq(start)
				.and(MemoryKeyNoteCache.COLUMN_DAY_END.eq(end));
			if (selection != null) {
				query.setSelection(selection);
			}
		}
		
		writer.delete(query);
	}
	
	public static long cacheKeyNotes(Context context,
			List<MemoryKeyNote> keynotes, long dayStart, long dayEnd) {
		if (context == null
				|| keynotes == null || keynotes.size() <= 0) {
			return 0l;
		}

		TimeCapsuleDatabaseWriter<MemoryKeyNoteCache> writer =
				new TimeCapsuleDatabaseWriter<MemoryKeyNoteCache>(context,
						MemoryKeyNoteCache.class);
		
		List<MemoryKeyNoteCache> caches = new ArrayList<MemoryKeyNoteCache>();
		
		MemoryKeyNoteCache c = null;
		for (MemoryKeyNote kn: keynotes) {
			c = MemoryKeyNoteCache.from(context, kn);
			c.setDayStart(dayStart);
			c.setDayEnd(dayEnd);
			
			caches.add(c);
		}
		
		if (caches.size() <= 0) {
			return 0l;
		}
				
		return writer.insert(caches.toArray(new MemoryKeyNoteCache[0]));
	}

}
