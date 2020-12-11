package com.dailystudio.memory.lifestyle.card;

import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardElements;

import android.content.Context;

public class MyselfCardsBuilder extends CardBuilder {
	
	private final static String MYSELF_CARD_TEMPL = "card_myself_templ.html";

	public MyselfCardsBuilder() {
		super(MYSELF_CARD_TEMPL, Cards.CARD_MYSELF_FILE);
	}
	
	@Override
	protected void buildCardElements(Context context, CardElements elements) {
	}

}
