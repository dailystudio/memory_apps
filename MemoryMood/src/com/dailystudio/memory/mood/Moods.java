package com.dailystudio.memory.mood;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;

public class Moods {
	
	private static Mood[] sMoods = {
		new Mood(1,	R.string.mood_01_label,		R.string.mood_01_digest, R.drawable.ic_mood_01, 5),
		new Mood(2,	R.string.mood_02_label,		R.string.mood_02_digest, R.drawable.ic_mood_02, 0),
		new Mood(3,	R.string.mood_03_label,		R.string.mood_03_digest, R.drawable.ic_mood_03, -4),
		new Mood(4,	R.string.mood_04_label,		R.string.mood_04_digest, R.drawable.ic_mood_04, 1),
		new Mood(5,	R.string.mood_05_label,		R.string.mood_05_digest, R.drawable.ic_mood_05, -5),
		new Mood(6,	R.string.mood_06_label,		R.string.mood_06_digest, R.drawable.ic_mood_06, -3),
		new Mood(7,	R.string.mood_07_label,		R.string.mood_07_digest, R.drawable.ic_mood_07, -1),
		new Mood(8,	R.string.mood_08_label,		R.string.mood_08_digest, R.drawable.ic_mood_08, 3),
		new Mood(9,	R.string.mood_09_label,		R.string.mood_08_digest, R.drawable.ic_mood_09, -2),
		new Mood(10, R.string.mood_10_label,	R.string.mood_10_digest, R.drawable.ic_mood_10, 2),
		new Mood(11, R.string.mood_11_label,	R.string.mood_11_digest, R.drawable.ic_mood_11, 4),
		new Mood(12, R.string.mood_12_label,	R.string.mood_12_digest, R.drawable.ic_mood_12, -3),
	};
	
	private static int sMinLevel = 0;
	private static int sMaxLevel = 0;
	private static int sMinLevelMoodId = 0;
	private static int sMaxLevelMoodId = 0;
	
	static {
		if (sMoods != null) {
			for (Mood mood: sMoods) {
				if (mood.level >= sMaxLevel) {
					sMaxLevel = mood.level;
					sMaxLevelMoodId = mood.identifier;
				} else if (mood.level < sMinLevel) {
					sMinLevel = mood.level;
					sMinLevelMoodId = mood.identifier;
				}
			}
		}
		
		Logger.debug("sMaxLevel[%d, id: %d], sMinLevel[%d, id: %d]",
				sMaxLevel, sMaxLevelMoodId,
				sMinLevel, sMinLevelMoodId);
	}
	
	public static List<Mood> listMoods() {
		List<Mood> moods = new ArrayList<Mood>();
		
		for (Mood face: sMoods) {
			moods.add(face);
		}
		
		return moods;
	}
	
	public static List<Integer> listMoodResources() {
		List<Integer> resIds = new ArrayList<Integer>();
		
		for (Mood mood: sMoods) {
			resIds.add(mood.iconResId);
		}
		
		return resIds;
	}
	
	public static Mood getMood(int identifier) {
		final int moodIndex = identifier - 1;
		if (moodIndex < 0 || moodIndex >= sMoods.length) {
			return null;
		}
		
		return sMoods[moodIndex];
	}
	
	public static int getMoodLevel(int identifier) {
		Mood mood = getMood(identifier);
		if (mood == null) {
			return 0;
		}
		
		return mood.level;
	}
	
	public static int getMinMoodLevel() {
		return sMinLevel;
	}
	
	public static int getMaxMoodLevel() {
		return sMaxLevel;
	}
	
	public static int findMoodIdByLevel(int moodLevel) {
		if (moodLevel <= sMinLevel) {
			return sMinLevelMoodId;
		} else if (moodLevel >= sMaxLevel) {
			return sMaxLevelMoodId;
		}
		
		if (sMoods == null) {
			return -1;
		}
		
		int minDeltaId = getDefaultMoodId();
		float minLevelDelta = (sMaxLevel - sMinLevel);
		float levelDelta = .0f;
		for (Mood mood: sMoods) {
			levelDelta = Math.abs((float)mood.level - moodLevel);
/*			Logger.debug("levelDelta(%f) = Math.abs[mood.lvl(%f) - moodLevel(%d)], min[delta(%f), id(%d)]", 
					levelDelta,
					(float)mood.level,
					moodLevel,
					minLevelDelta,
					minDeltaId);
*/
			if (levelDelta < minLevelDelta) {
				minLevelDelta = levelDelta;
				minDeltaId = mood.identifier;
			}
		}
		
		return minDeltaId;
	}
	
	public static Mood getDefaultMood() {
		return getMood(getDefaultMoodId());
	}
	
	public static int getDefaultMoodId() {
		return 4;
	}
	
}
