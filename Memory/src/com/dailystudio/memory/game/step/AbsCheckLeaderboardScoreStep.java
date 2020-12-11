package com.dailystudio.memory.game.step;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.game.MemoryGameUtils;
import com.dailystudio.memory.loader.MemoryLoaderStep;

import android.content.Context;

public abstract class AbsCheckLeaderboardScoreStep extends MemoryLoaderStep {

	public AbsCheckLeaderboardScoreStep(Context context) {
		super(context);
	}
	
	@Override
	public boolean loadInBackground() {
		final long score = getLeaderboardScore();
		final String lid = getLeaderboardId();
		
		Logger.debug("[CHECK LEADERBOARD]: %s, score = %s",
				lid, score);
		if (score > 0) {
			MemoryGameUtils.submitLeaderboardScore(
					mContext, lid, score);
		}
		
		return true;
	}
	
	abstract public String getLeaderboardId();
	abstract protected long getLeaderboardScore();

}
