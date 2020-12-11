package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.querypieces.quote.QuoteQuery;
import com.dailystudio.memory.querypieces.quote.QuoteDigest;

public class QueryQuoteLoader extends AbsAsyncDataLoader<QuoteDigest> {

	public QueryQuoteLoader(Context context) {
		super(context);
	}

	
	@Override
	public QuoteDigest loadInBackground() {
		final Context context = getContext();
		
		QuoteDigest digest = null;
		
		QuoteQuery quoteQuery = new QuoteQuery();
		if (quoteQuery != null) {
			digest = quoteQuery.getLatestQuote(context);
		}
		
		return digest;
	}
	
}
