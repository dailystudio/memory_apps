package com.dailystudio.memory.mood;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dailystudio.memory.activity.MemoryCoreAppCheckActivity;

public class MoodCoreAppCheckActivity extends MemoryCoreAppCheckActivity {

	@Override
	protected Drawable getPluginIcon() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_mood);
	}

	@Override
	protected Drawable getPluginIconDisabled() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_mood_disabled);
	}

	@Override
	protected CharSequence getPluginName() {
		return getString(R.string.category_mood_label);
	}

}
