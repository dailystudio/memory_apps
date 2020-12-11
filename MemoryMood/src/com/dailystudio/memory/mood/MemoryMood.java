package com.dailystudio.memory.mood;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.datetime.dataobject.TimeCapsule;

public class MemoryMood extends TimeCapsule {
	
	public static final Column COLUMN_MOOD = new IntegerColumn("mood", false);
	public static final Column COLUMN_MOOD_LEVEL = new IntegerColumn("mood_level", false);
	public static final TextColumn COLUMN_COMMENTS = new TextColumn("comment");

	private final static Column[] sColumns = {
		COLUMN_MOOD,
		COLUMN_MOOD_LEVEL,
		COLUMN_COMMENTS,
	};

	public MemoryMood(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		templ.addColumns(sColumns);
	}

	public void setMood(int moodIdentifier) {
		setValue(COLUMN_MOOD, moodIdentifier);
		
		final int lvl = Moods.getMoodLevel(moodIdentifier);
		setValue(COLUMN_MOOD_LEVEL, lvl);
	}
	
	public int getMood() {
		return getIntegerValue(COLUMN_MOOD);
	}

	public int getMoodLevel() {
		return getIntegerValue(COLUMN_MOOD_LEVEL);
	}

	public void setComment(String comment) {
		setValue(COLUMN_COMMENTS, comment);
	}
	
	public String getComment() {
		return getTextValue(COLUMN_COMMENTS);
	}

	@Override
	public String toString() {
		return String.format("%s, moode = [%d, lvl: %d], comment = %s",
				super.toString(),
				getMood(),
				getMoodLevel(),
				getComment());
	}
		
}
