package com.dailystudio.memory.querypieces.quote;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.QueryPieceDigest;
import com.google.gson.Gson;

import android.content.Context;

public class QuoteQuery extends QueryPieceDigest<QuoteDigest> {
	
	private static final String ARG_QUOTE = "latest-quote";

	@Override
	protected QuoteDigest parseResult(Context context, MemoryPieceDigest[] digests) {
		if (digests == null || digests.length <= 0) {
			return null;
		}
		
		MemoryPieceDigest digest0 = digests[0];
		Logger.debug("digest0 = %s", digest0);
		
		String quoteJson = digest0.getContent();
		
		Gson gson = new Gson();
		
		QuoteDigest quote = gson.fromJson(quoteJson, QuoteDigest.class);
		
		return quote;
	}

	public QuoteDigest getLatestQuote(Context context) {
		return doQuery(context, ARG_QUOTE);
	}

}
