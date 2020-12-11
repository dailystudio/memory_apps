package com.dailystudio.memory.where.card;

import android.content.Context;

import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardsUpdater;

public class WhereCardsUpdater extends CardsUpdater {
	
	public WhereCardsUpdater(Context context, String cardName) {
		super(context, cardName);
	}

	protected CardBuilder getCardBuilder(String cardName) {
		if (Cards.CARD_MY_PLACES_PIE_CHART.equals(cardName)) {
			return new MyPlacesPieChartCardBuilder(getContext());
		} else if (Cards.CARD_IDSPOT_NOW.equals(cardName)) {
			return new IdspotNowCardBuilder();
		}
		
		return null;
	}

}
