package com.dailystudio.memory.database.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypieces.AllCards;

public class CardsLoader extends AbsAsyncDataLoader<List<MemoryPieceCard>> {
	
	public CardsLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<MemoryPieceCard> loadInBackground() {
		AllCards cards = new AllCards();
		
		return cards.queryCards(getContext());
	}

}
