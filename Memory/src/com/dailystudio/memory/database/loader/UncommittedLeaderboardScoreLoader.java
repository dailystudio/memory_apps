package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.memory.game.UncommittedLeaderboardScore;

public class UncommittedLeaderboardScoreLoader
	extends DatabaseObjectsLoader<UncommittedLeaderboardScore> {

	public UncommittedLeaderboardScoreLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<UncommittedLeaderboardScore> getObjectClass() {
		return UncommittedLeaderboardScore.class;
	}

}
