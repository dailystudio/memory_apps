package com.dailystudio.memory.card;

import com.dailystudio.memory.R;
import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardElements;

import android.content.Context;

public class DefaultCardBuilder extends CardBuilder {
	
	private final static String DEFAULT_CARD_TEMPL = "default_card_templ.html";

	private final static String RPL_KEY_TITLE = "default_card_title";
	private final static String RPL_KEY_CONTENT = "default_card_content";
	
	public DefaultCardBuilder() {
		super(DEFAULT_CARD_TEMPL, Cards.CARD_DEFAULT_FILE);
	}
	
	@Override
	protected void buildCardElements(Context context, CardElements elements) {
		String titleVal = context.getString(R.string.default_card_title);
		String contentVal = context.getString(R.string.default_card_content);
    	
    	elements.putElement(RPL_KEY_TITLE, titleVal);
    	elements.putElement(RPL_KEY_CONTENT, contentVal);
	}

}
