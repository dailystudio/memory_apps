package com.dailystudio.memory.where;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.plugin.MemoryPlugin;

import android.content.Context;
import android.content.Intent;

public class PluginWhere extends MemoryPlugin {

	@Override
	public boolean onCreateTask(Context context, int taskId, String klass,
			long now) {
		Logger.debug("taskId = %d, klass = %s, now = %s", 
				taskId,
				klass,
				now);
		return super.onCreateTask(context, taskId, klass, now);
	}
	
	@Override
	protected Intent getKeepAliveTaskServiceIntent(Context context) {
		if (context == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		i.setClass(context.getApplicationContext(), WhereTasksKeepAliveSerive.class);

		return i;
	}

}
