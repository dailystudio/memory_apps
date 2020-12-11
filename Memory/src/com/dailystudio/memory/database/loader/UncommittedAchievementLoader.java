package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.memory.game.UncommittedAchievement;

public class UncommittedAchievementLoader
	extends DatabaseObjectsLoader<UncommittedAchievement> {

	public UncommittedAchievementLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<UncommittedAchievement> getObjectClass() {
		return UncommittedAchievement.class;
	}

}
