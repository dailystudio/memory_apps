package com.dailystudio.memory.application.appwidget;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.appwidget.ConvertedAppWidgetDataAsyncTask;
import com.dailystudio.memory.appwidget.MemoryAppWidgetDataService;

import android.content.Intent;
import android.os.IBinder;

public class FavoriteAppsWidgetDataService extends MemoryAppWidgetDataService {

	private final static Class<?>[] DB_OBJ_CLASSES = {
		FavoriteApp.class,
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
		Logger.debug("objectClass = %s", objectClass);
		ConvertedAppWidgetDataAsyncTask<?, ?, ?> asyncTask = null;
		
		List<ConvertedAppWidgetDataAsyncTask<?, ?, ?>> tasks = 
				new ArrayList<ConvertedAppWidgetDataAsyncTask<?,?,?>>();
		
		if (objectClass == FavoriteApp.class) {
			asyncTask = new FavoriteAppsWidgetDataAsyncTask(
					getApplicationContext());
			tasks.add(asyncTask);
			
			asyncTask = new HourlyFavoriteAppsWidgetDataAsyncTask(
					getApplicationContext());
			tasks.add(asyncTask);
		}
		
		return tasks;
	}

}
