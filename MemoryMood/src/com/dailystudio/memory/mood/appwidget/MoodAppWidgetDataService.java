package com.dailystudio.memory.mood.appwidget;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.memory.appwidget.ConvertedAppWidgetDataAsyncTask;
import com.dailystudio.memory.appwidget.MemoryAppWidgetDataService;
import com.dailystudio.memory.mood.MemoryMood;

import android.content.Intent;
import android.os.IBinder;

public class MoodAppWidgetDataService extends MemoryAppWidgetDataService {

	private final static Class<?>[] DB_OBJ_CLASSES = {
			MemoryMood.class,
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends DatabaseObject>[] listObservedDatabaseObject() {
		return (Class<? extends DatabaseObject>[])DB_OBJ_CLASSES;
	}
	
	@Override
	protected List<ConvertedAppWidgetDataAsyncTask<?, ?, ?>> createAsyncTasks(
			Class<? extends DatabaseObject> objectClass) {
		ConvertedAppWidgetDataAsyncTask<?, ?, ?> asyncTask = null;
		
		List<ConvertedAppWidgetDataAsyncTask<?, ?, ?>> tasks = 
				new ArrayList<ConvertedAppWidgetDataAsyncTask<?,?,?>>();
		
		if (objectClass == MemoryMood.class) {
			asyncTask = new MoodDayChartWidgetDataAsyncTask(
					getApplicationContext(), -1);
			tasks.add(asyncTask);
		}
		
		return tasks;
	}

}
