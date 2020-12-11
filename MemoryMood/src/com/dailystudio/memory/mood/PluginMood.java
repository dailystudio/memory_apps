package com.dailystudio.memory.mood;

import com.dailystudio.memory.mood.appwidget.MoodAppWidgetDataService;
import com.dailystudio.memory.plugin.MemoryPlugin;

import android.content.Context;
import android.content.Intent;

public class PluginMood extends MemoryPlugin {

	@Override
	protected Intent getKeepAliveTaskServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), MoodTasksKeepAliveSerive.class);

		return i;
	}

	@Override
	protected Intent getAppWidgetDataServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), MoodAppWidgetDataService.class);

		return i;
	}

}
