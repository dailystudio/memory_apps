package com.dailystudio.memory.stats;

import java.util.List;

import android.content.Context;

import com.dailystudio.memory.querypiece.MemoryPieceCountQuery;

public class StatsPieces extends MemoryPieceCountQuery<Long> {
	
	private Context mContext;
	
	public StatsPieces(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public long statisticsPieces() {
		return queryCounts(getContext());
	}

	@Override
	protected Long parseResults(Context context, List<Long> pieces) {
		if (pieces == null || pieces.size() <= 0) {
			return 0l;
		}
		
		long totalPieces = 0l;
		for (Long lnum: pieces) {
			totalPieces += lnum;
		}
		
		return totalPieces;
	}
	
}
