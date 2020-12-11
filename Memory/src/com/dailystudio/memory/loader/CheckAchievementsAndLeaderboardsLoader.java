package com.dailystudio.memory.loader;

import android.content.Context;

import com.dailystudio.memory.game.step.CheckFirstPieceAchievementStep;
import com.dailystudio.memory.game.step.CheckPiecesLeaderboardScoreStep;

public class CheckAchievementsAndLeaderboardsLoader extends MemoryStepLoader { 
	
	public CheckAchievementsAndLeaderboardsLoader(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		addStep(new CheckFirstPieceAchievementStep(mContext));
		addStep(new CheckPiecesLeaderboardScoreStep(mContext));
	}

}
