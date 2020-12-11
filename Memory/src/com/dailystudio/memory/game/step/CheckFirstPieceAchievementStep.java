package com.dailystudio.memory.game.step;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.stats.StatsPieces;

import android.content.Context;

public class CheckFirstPieceAchievementStep extends AbsCheckAchievementStep {

	public CheckFirstPieceAchievementStep(Context context) {
		super(context);
	}

	@Override
	public String getAchievementId() {
		return mContext.getString(R.string.achievement_first_piece);
	}

	@Override
	protected boolean isAchievementAccomplished() {
		StatsPieces statsPieces = new StatsPieces(mContext);
		
		final long count = statsPieces.statisticsPieces();
		Logger.debug("piecesCount = %d", count);
		
		return (count > 0);
	}
	
}
