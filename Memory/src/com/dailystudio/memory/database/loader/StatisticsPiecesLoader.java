package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.dataobject.CountObject;
import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.stats.StatsPieces;

public class StatisticsPiecesLoader extends AbsAsyncDataLoader<CountObject> {

	public StatisticsPiecesLoader(Context context) {
		super(context);
	}
	
	@Override
	public CountObject loadInBackground() {
		final Context context = getContext();
		
		StatsPieces statPieces = new StatsPieces(context);
		
		final long piecesCount = statPieces.statisticsPieces();
		
		CountObject co = new CountObject(context);
		
		co.setCount((int)piecesCount);
		
		return co;
	}
	
}
