package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.memory.boot.MemoryBoot;

public class StatisticsLifetimeLoader extends AbsAsyncDataLoader<TimeCapsule> {

	public StatisticsLifetimeLoader(Context context) {
		super(context);
	}

	
	@Override
	public TimeCapsule loadInBackground() {
		Query query = new Query(MemoryBoot.class);
		
		OrderingToken orderByToken = 
			MemoryBoot.COLUMN_ID.orderByAscending();
		if (orderByToken == null) {
			return null;
		}
		
		query.setOrderBy(orderByToken);
		
		ExpressionToken limitToken = 
			new ExpressionToken(1);
		
		query.setLimit(limitToken);
		
		DatabaseReader<MemoryBoot> reader = 
			new DatabaseReader<MemoryBoot>(getContext(), MemoryBoot.class);

		TimeCapsule object = reader.queryLastOne(query);
		
		return object;
	}
	
}
