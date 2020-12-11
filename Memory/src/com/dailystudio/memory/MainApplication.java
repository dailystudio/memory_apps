package com.dailystudio.memory;

import android.content.Intent;

import com.dailystudio.app.DevBricksApplication;
import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.memory.game.UncommittedAchievement;
import com.dailystudio.memory.card.Cards;
import com.dailystudio.memory.card.DefaultCardsUpdater;

public class MainApplication extends DevBricksApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		
		new DefaultCardsUpdater(getApplicationContext(), 
				Cards.CARD_DEFAULT).doUpdate();

		UncommittedAchievement.setEncryptPassword(getString(
				R.string.memory_game_app_id));

		Intent srvIntent = new Intent(MemoryService.SERVICE_INTENT);

		srvIntent.setClass(getApplicationContext(),
				MemoryService.class);

		startService(srvIntent);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		ThumbCacheManager.clear();
	}

	@Override
	protected boolean isDebugBuild() {
		return BuildConfig.DEBUG;
	}

}
