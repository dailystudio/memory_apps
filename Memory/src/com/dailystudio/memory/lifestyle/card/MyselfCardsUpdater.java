package com.dailystudio.memory.lifestyle.card;

import android.content.Context;

import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardsUpdater;

public class MyselfCardsUpdater extends CardsUpdater {
	
	public MyselfCardsUpdater(Context context, String cardName) {
		super(context, cardName);
	}

	protected CardBuilder getCardBuilder(String cardName) {
		if (Cards.CARD_MYSELF.equals(cardName)) {
			return new MyselfCardsBuilder();
		}
		
		return null;
	}

}
