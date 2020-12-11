package com.dailystudio.memory.mood;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.memory.querypiece.MemoryPieceQueryReceiver;

public class MemoryMoodPieceQueryReceiver extends MemoryPieceQueryReceiver {

	@Override
	protected boolean supportCountPiece(Context context) {
		return true;
	}

	@Override
	protected boolean supportDigestPiece(Context context) {
		return true;
	}

	@Override
	protected boolean supportCardPiece(Context context) {
		return false;
	}

	@Override
	protected ComponentName getQueryService(Context context) {
		return new ComponentName(context, 
				MemoryMoodPieceQueryService.class);
	}

}
