package com.dailystudio.memory.card;

import android.content.Context;

import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardsUpdater;

public class DefaultCardsUpdater extends CardsUpdater {
	
	public DefaultCardsUpdater(Context context, String cardName) {
		super(context, cardName);
	}

	protected CardBuilder getCardBuilder(String cardName) {
		if (Cards.CARD_DEFAULT.equals(cardName)) {
			return new DefaultCardBuilder();
		}
		
		return null;
	}

}
