package com.dailystudio.memory.game.step;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.game.MemoryGameUtils;
import com.dailystudio.memory.loader.MemoryLoaderStep;

import android.content.Context;

public abstract class AbsCheckAchievementStep extends MemoryLoaderStep {

	public AbsCheckAchievementStep(Context context) {
		super(context);
	}
	
	@Override
	public boolean loadInBackground() {
		final boolean ret = isAchievementAccomplished();
		final String aid = getAchievementId();
		
		Logger.debug("[CHECK ACHIEVEMENT]: %s, result = %s",
				aid, ret);
		if (ret) {
			MemoryGameUtils.unlockAchievement(mContext,
					getAchievementId());
		}
		
		return true;
	}
	
	abstract public String getAchievementId();
	abstract protected boolean isAchievementAccomplished();

}
