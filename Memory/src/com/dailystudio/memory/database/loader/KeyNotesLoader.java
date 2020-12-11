package com.dailystudio.memory.database.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.querypiece.MemoryKeyNote;
import com.dailystudio.memory.querypieces.AllKeynotes;

public class KeyNotesLoader extends AbsAsyncDataLoader<List<MemoryKeyNote>> {
	
	private long mPeroidStart;
	private long mPeroidEnd;
	
	public KeyNotesLoader(Context context, long start, long end) {
		super(context);
		
		mPeroidStart = start;
		mPeroidEnd = end;
	}
	
	@Override
	public List<MemoryKeyNote> loadInBackground() {
		AllKeynotes allKeynotes = new AllKeynotes(mPeroidStart, mPeroidEnd);
		
		return allKeynotes.queryKeynotes(getContext());
	}

}
