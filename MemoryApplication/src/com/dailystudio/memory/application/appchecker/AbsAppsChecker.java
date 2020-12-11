package com.dailystudio.memory.application.appchecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dailystudio.app.async.AsyncTasksQueueExecutor;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

public abstract class AbsAppsChecker extends AsyncTasksQueueExecutor {
	
	protected abstract class BaseCheckAsyncTask 
		extends QueuedAsyncTask<Void, Void, Void> {
		
		protected Context mContext;
		
		protected BaseCheckAsyncTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if (mContext == null) {
				return null;
			}
			
			final long now = System.currentTimeMillis();
			final long lastTimestamp = getLastCheckTimestamp(mContext);
		
			doAppsCheck(now, lastTimestamp);
			
			Logger.debug("Check done: now = %d(%s)",
					now,
					CalendarUtils.timeToReadableString(now));
			
			setLastCheckTimestamp(mContext, now);
			
			return null;
		}
		
		abstract protected void doAppsCheck(long now, long lastTimestamp);

	}

	public long getLastCheckTimestamp(Context context) {
		if (context == null) {
			return -1l;
		}
		
		final String prefName = getCheckPrefName(context);
		if (prefName == null) {
			return -1l;
		}
		
		final String timestampKey = getCheckTimestampKey(context);
		if (timestampKey == null) {
			return -1l;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(
				prefName, Context.MODE_PRIVATE);
		if (pref == null) {
			return -1l;
		}
		
		return pref.getLong(timestampKey, -1l);
	}
	
	public void setLastCheckTimestamp(Context context, long timestamp) {
		if (context == null || timestamp < 0) {
			return;
		}
		
		final String prefName = getCheckPrefName(context);
		if (prefName == null) {
			return;
		}
		
		final String timestampKey = getCheckTimestampKey(context);
		if (timestampKey == null) {
			return;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(
				prefName, Context.MODE_PRIVATE);
		if (pref == null) {
			return;
		}
		
		final Editor editor = pref.edit();
		if (editor == null) {
			return;
		}
		
		editor.putLong(timestampKey, timestamp);
		editor.commit();
	}
	
	abstract protected String getCheckPrefName(Context context);
	abstract protected String getCheckTimestampKey(Context context);
	
}
