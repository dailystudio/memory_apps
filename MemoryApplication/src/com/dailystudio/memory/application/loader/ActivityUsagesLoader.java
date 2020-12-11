package com.dailystudio.memory.application.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.ProjectedDatabaseObjectsLoader;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.ActivityUsageStatistics;

@Deprecated
public class ActivityUsagesLoader extends ProjectedDatabaseObjectsLoader<Usage, ActivityUsageStatistics> {

	@SuppressWarnings("unused")
	private String mPackage;

	public ActivityUsagesLoader(Context context, String pkgName) {
		super(context);
		
		mPackage = pkgName;
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
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
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
	
}
