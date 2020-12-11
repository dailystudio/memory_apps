package com.dailystudio.memory.game.step;

import com.dailystudio.memory.R;
import com.dailystudio.memory.stats.StatsPieces;

import android.content.Context;

public class CheckPiecesLeaderboardScoreStep extends AbsCheckLeaderboardScoreStep {

	public CheckPiecesLeaderboardScoreStep(Context context) {
		super(context);
	}

	@Override
	public String getLeaderboardId() {
		return mContext.getString(R.string.leaderboard_memory_collector);
	}

	@Override
	protected long getLeaderboardScore() {
		StatsPieces statsPieces = new StatsPieces(mContext);
		
		long count = statsPieces.statisticsPieces();

		return count;
	}

}
