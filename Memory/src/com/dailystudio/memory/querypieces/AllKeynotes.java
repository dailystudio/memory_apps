package com.dailystudio.memory.querypieces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.querypiece.MemoryKeyNote;
import com.dailystudio.memory.querypiece.MemoryKeyNoteExtra;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceDigestQuery;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;

public class AllKeynotes extends MemoryPieceDigestQuery<List<MemoryKeyNote>> {

	private long mKeyNotesStart;
	private long mKeyNotesEnd;
	
	public AllKeynotes() {
		this(-1, -1);
	}
	
	public AllKeynotes(long start, long end) {
		mKeyNotesStart = start;
		mKeyNotesEnd = end;
	}
	
	public List<MemoryKeyNote> queryKeynotes(Context context) {
		String queryArgs = MemoryPieceKeyNotes.composeTimeSpan(mKeyNotesStart,
				mKeyNotesEnd);
		Logger.debug("keynotes.queryArgs = %s", queryArgs);
		
		List<MemoryKeyNote> keynotes = queryDigests(context, queryArgs);
		if (keynotes == null) {
			keynotes = new ArrayList<MemoryKeyNote>();
		}
		
		MemoryKeyNote kn = null;
		
		kn = new MemoryKeyNote(
				CalendarUtils.getStartOfDay(mKeyNotesStart),
				context.getString(R.string.key_note_start_of_day));
		keynotes.add(kn);
		
		if (keynotes.size() == 1) {
			kn = new MemoryKeyNote(
					CalendarUtils.getEndOfDay(mKeyNotesStart),
					context.getString(R.string.key_note_end_of_day));
			keynotes.add(0, kn);
		}

		return keynotes;
	}

	@Override
	protected List<MemoryKeyNote> parseResults(Context context,
			List<MemoryPieceDigest> pieces) {
		Logger.debug("keynotes-pieces = %s", pieces);
		if (pieces == null) {
			return null;
		}
		
		final int N = pieces.size();
		if (N <= 0) {
			return null;
		}
		
		List<MemoryKeyNote> notes = new ArrayList<MemoryKeyNote>();
		
		MemoryKeyNote kn = null;
		Object extraObj = null;
		MemoryPieceDigest digest = null;
		
		for (int i = 0; i < N; i++) {
			digest = pieces.get(i);
			
			kn = new MemoryKeyNote(digest.getTimestamp(),
					digest.getContent());
			
			extraObj = digest.getExtraData(MemoryKeyNoteExtra.class);
			if (extraObj instanceof MemoryKeyNoteExtra) {
				kn.setOverTheDayEnd(((MemoryKeyNoteExtra)extraObj).overTheDayEnd);
			}
			
			notes.add(kn);
		}
		
		Collections.sort(notes, new MemoryKeyNote.MemoryKeyNoteComparator());
		
		return notes;
	}

}
