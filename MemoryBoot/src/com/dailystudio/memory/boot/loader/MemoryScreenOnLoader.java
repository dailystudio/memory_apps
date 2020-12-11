package com.dailystudio.memory.boot.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.boot.MemoryScreenOn;
import com.dailystudio.memory.loader.PeroidDatabaseCursorLoader;

public class MemoryScreenOnLoader extends PeroidDatabaseCursorLoader {

	private int[] mWeekdays = null;
	
	public MemoryScreenOnLoader(Context context, 
			long start, long end, int[] weekdays) {
		super(context, start, end);
		
		mWeekdays = weekdays;
	}

	@Override
	protected Class<? extends DatabaseObject> getObjectClass() {
		return MemoryScreenOn.class;
	}
	
	@Override
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		Query query = super.getQuery(klass);
		if (query == null) {
			return query;
		}
		
		if (mWeekdays != null && mWeekdays.length > 0) {
			List<Integer> days = new ArrayList<Integer>();
			
			for (int weekday: mWeekdays) {
				days.add(weekday);
			}
			
			ExpressionToken weekdaysSelToken = 
					MemoryScreenOn.COLUMN_TIME.WEEKDAY().inValues(
							days.toArray(new Integer[0]));
			
			ExpressionToken selToken = query.getSelection();
			if (selToken == null) {
				selToken = weekdaysSelToken;
			} else {
				selToken.and(weekdaysSelToken);
			}
			
			query.setSelection(selToken);
		}

		OrderingToken orderByToken =
			TimeCapsule.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return query;
	}
	
}
