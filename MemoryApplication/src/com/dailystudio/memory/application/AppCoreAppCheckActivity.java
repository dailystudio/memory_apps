package com.dailystudio.memory.application;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dailystudio.memory.activity.MemoryCoreAppCheckActivity;

public class AppCoreAppCheckActivity extends MemoryCoreAppCheckActivity {

	@Override
	protected Drawable getPluginIcon() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_app);
	}

	@Override
	protected Drawable getPluginIconDisabled() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_app_disabled);
	}

	@Override
	protected CharSequence getPluginName() {
		return getString(R.string.category_app_label);
	}

}
