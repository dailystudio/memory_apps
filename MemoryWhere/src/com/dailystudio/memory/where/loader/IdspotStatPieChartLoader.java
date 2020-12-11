package com.dailystudio.memory.where.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.loader.DatabaseChartLoader;
import com.dailystudio.memory.where.chart.IdspotStatPieChartBuilder;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;

public class IdspotStatPieChartLoader extends DatabaseChartLoader<IdspotHistory> {

	private IdspotStatPieChartBuilder mChartBuilder;

	public IdspotStatPieChartLoader(Context context, 
			long start, long end) {
		super(context, start, end);
		Logger.debug("[%s - %s]", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
		
		mChartBuilder = new IdspotStatPieChartBuilder(context, start, end);
	}

	@Override
	protected Class<IdspotHistory> getObjectClass() {
		return IdspotHistory.class;
	}
	
	@Override
	public Object createShareArguments() {
		return mChartBuilder.createShareArguments();
	}
	
	@Override
	protected Query getQuery(Class<IdspotHistory> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		
		if (end <= start) {
			return super.getQuery(getObjectClass());
		}

		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(getObjectClass());
		
		Query query = builer.getQueryForIntersect(
				IdspotHistory.COLUMN_TIME, 
				IdspotHistory.COLUMN_DURATION,
				start, end);	
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
				query.getSelection();
		
		IdspotHistory history = 
				IdspotHistoryDatabaseModal.getLastHistory(getContext());
		if (history == null || history.getDuration() > 0) {
			return query;
		}
		
		ExpressionToken moreSelToken = null;
		if (history.getTime() < end) {
			moreSelToken = IdspotHistory.COLUMN_ID.eq(history.getId());
		}
		
		if (moreSelToken != null) {
			if (selToken == null) {
				selToken = moreSelToken;
			} else {
				selToken = selToken.or(moreSelToken);
			}
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		return query;
	}
	
	@Override
	protected Object buildDataset(List<IdspotHistory> objects,
			Object sharedArguments) {
		return mChartBuilder.buildDataset(objects, sharedArguments);
	}
	
	@Override
	protected Object buildRenderer(List<IdspotHistory> objects,
			Object sharedArguments) {
		return mChartBuilder.buildRenderer(objects, sharedArguments);
	}

}