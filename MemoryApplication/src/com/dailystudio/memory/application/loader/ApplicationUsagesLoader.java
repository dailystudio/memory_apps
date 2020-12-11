package com.dailystudio.memory.application.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.ActivityUsageStatistics;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.loader.ConvertedPeroidDatabaseObjectsLoader;

public class ApplicationUsagesLoader 
	extends ConvertedPeroidDatabaseObjectsLoader<Usage, ActivityUsageStatistics, UsageStatistics> {

	private Integer[] mFilterCompIds;
	private int mFilterFlags;
	private ActionBarActivity mHostActivity;
	
	public ApplicationUsagesLoader(Context context, long start, long end, int filterFlags) {
		super(context, start, end);
		
		if (context instanceof ActionBarActivity) {
			mHostActivity = (ActionBarActivity)context;
		}
		
		mFilterFlags = filterFlags;
	}

	@Override
	protected Class<Usage> getObjectClass() {
		return Usage.class;
	}
	
	@Override
	protected Class<ActivityUsageStatistics> getProjectionClass() {
		return ActivityUsageStatistics.class;
	}
	
	@Override
	protected Query getQuery(Class<Usage> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		Logger.debug("loader[%s], peroid = [%s - %s]", 
				this,
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		Query query = null;
		
		if (end <= start) {
			query = super.getQuery(getObjectClass());
		} else {
			TimeCapsuleQueryBuilder builer =
				new TimeCapsuleQueryBuilder(getObjectClass());
			
			query = builer.getQueryForIntersect(
					Usage.COLUMN_TIME, 
					Usage.COLUMN_DURATION,
					start, end);
		}
		
		if (mFilterCompIds != null && mFilterCompIds.length > 0) {
			ExpressionToken selCompIdsToken = 
					Usage.COLUMN_COMPONENT_ID.outOfValues(mFilterCompIds);
			Logger.debug("selCompIdsToken = %s", selCompIdsToken);
			
			ExpressionToken selToken = query.getSelection();

			if (selCompIdsToken != null) {
				if (selToken == null) {
					selToken = selCompIdsToken;
				} else {
					selToken = selToken.and(selCompIdsToken);
				}
				
				query.setSelection(selToken);
			}
		}
		
		OrderingToken groupBy = Usage.COLUMN_COMPONENT_ID.groupBy();
		if (groupBy != null) {
			query.setGroupBy(groupBy);
		}
		
		OrderingToken orderByToken = ActivityUsageStatistics.COLUMN_DURATION_SUM.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}

		return query;
	}
	
	@Override
	protected List<UsageStatistics> onLoadInBackground() {
		final Context context = getContext();
		
		showPrompt();
		updateHostProgress(8);

		if (mFilterFlags == Constants.SYSTEM_APP_ONLY_FILTER_FLAGS) {
			mFilterCompIds = UsageComponentDatabaseModal.getUserComponentIds(context);
		} else if (mFilterFlags == Constants.USER_APP_ONLY_FILTER_FLAGS) {
			mFilterCompIds = UsageComponentDatabaseModal.getSystemComponentIds(context);
		} else {
			mFilterCompIds = null;
		}
		
		updateHostProgress(15);
		
		final List<UsageStatistics> actstats =
			super.onLoadInBackground();

		updateHostProgress(25);
		
		List<UsageStatistics> results = 
				ActivityUsageStatistics.convertToApplicationUsageStatistics(
						context, actstats);
		
		updateHostProgress(50);
		
		return results;
	}
	
	private void showPrompt() {
		if (mHostActivity != null
				&& !isAbandoned()
				&& !isReset()) {
			CharSequence promptstr = 
					getContext().getString(R.string.app_usage_load_prompt);
			
			mHostActivity.showPrompt(promptstr);
		}
	}
	
	private void updateHostProgress(int progress) {
/*		Logger.debug("loader[%s], prg =%d ,[reset = %s, abandon = %s]", 
				this, progress, isReset(), isAbandoned());
*/		
		if (mHostActivity != null
				&& !isAbandoned()
				&& !isReset()) {
			mHostActivity.setActionBarProgress(progress);
		}
	}
	
}
