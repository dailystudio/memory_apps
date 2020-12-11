package com.dailystudio.memory.mood;

import java.util.Comparator;

public class Mood {

	public static class MoodComparator implements Comparator<Mood> {

		@Override
		public int compare(Mood object1, Mood object2) {
			if (object1 == null) {
				return -1;
			} else if (object2 == null) {
				return 1;
			}
			
			return (object2.level - object1.level);
		}
		
	}

	
	public int identifier;
	public int labelResId;
	public int digestResId;
	public int iconResId;
	
	public int level;
	
	public Mood(int id, int labelRes, int digestRes, int iconRes, int lvl) {
		identifier = id;
		labelResId = labelRes;
		digestResId = digestRes;
		iconResId = iconRes;
		
		level = lvl;
	}
	
	@Override
	public String toString() {
		return String.format("identifier(%d), labelResId(%d, digest: %d), iconResId(%d), level(%d)", 
				identifier,
				labelResId,
				iconResId,
				level);
	}
	
}
