package com.dailystudio.memory.querypieces;

import java.util.List;

import android.content.Context;

import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypiece.MemoryPieceCardQuery;

public class AllCards extends MemoryPieceCardQuery<List<MemoryPieceCard>>{

	@Override
	protected List<MemoryPieceCard> parseResults(Context context,
			List<MemoryPieceCard> pieces) {
		return pieces;
	}

}
