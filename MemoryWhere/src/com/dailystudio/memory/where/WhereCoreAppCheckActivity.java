package com.dailystudio.memory.where;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dailystudio.memory.activity.MemoryCoreAppCheckActivity;

public class WhereCoreAppCheckActivity extends MemoryCoreAppCheckActivity {

	@Override
	protected Drawable getPluginIcon() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_where);
	}

	@Override
	protected Drawable getPluginIconDisabled() {
		final Resources res = getResources();
		if (res == null) {
			return null;
		}
		
		return res.getDrawable(R.drawable.ic_where_disabled);
	}

	@Override
	protected CharSequence getPluginName() {
		return getString(R.string.category_where_label);
	}

}
