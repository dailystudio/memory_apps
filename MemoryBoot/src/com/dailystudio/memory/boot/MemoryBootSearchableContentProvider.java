package com.dailystudio.memory.boot;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.searchable.MemorySearchableContent;
import com.dailystudio.memory.searchable.MemorySearchableContentProvider;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.MemorySearchableSuggestion;
import com.dailystudio.memory.searchable.queryparams.TimeQueryParameter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MemoryBootSearchableContentProvider 
	extends MemorySearchableContentProvider {

	private final static String SEARCHABLE_AUTHORITY = 
			"com.dailystudio.memory.boot.searchable";

	@Override
	protected List<MemorySearchableSuggestion> doQuerySuggestions(
			MemorySearchableQuery query) {
		final Cursor c = doQueryData(query);
		if (c == null || c.getCount() <= 0) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		List<MemorySearchableSuggestion> suggestions = 
				new ArrayList<MemorySearchableSuggestion>();
		
		MemorySearchableSuggestion suggestion = 
				new MemorySearchableSuggestion();
		
		suggestion.text1 = context.getString(R.string.category_boot_label);
		suggestion.text2 = String.format(
				context.getString(R.string.default_searchable_matches_templ), 
				c.getCount());
		suggestion.icon1ResId = composeResourceUri(context, R.drawable.ic_boot);
		
		suggestion.intentExtraData = String.valueOf(c.getCount());
		
		suggestions.add(suggestion);
		
		c.close();
		
		return suggestions;
	}

	@Override
	protected Cursor doQueryData(MemorySearchableQuery searchQuery) {
		if (searchQuery == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}

		DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(getContext(),
						Constants.MEMORY_DATABASE_AUTHORITY,
						MemoryBoot.class);
		
		Query query = new Query(MemoryBoot.class);
		
		final List<TimeQueryParameter> timeParams = 
				searchQuery.timeQueryParams;
		
		ExpressionToken selToken = null;
		
		ExpressionToken subToken = null;
		for (TimeQueryParameter tqp: timeParams) {
			subToken = MemoryBoot.COLUMN_TIME.gte(tqp.timeBegin)
					.and(MemoryBoot.COLUMN_TIME.lte(tqp.timeEnd));
			if (selToken == null) {
				selToken = subToken;
			} else {
				selToken = selToken.or(subToken);
			}
		}
		
		if (selToken == null
				|| TextUtils.isEmpty(selToken.toString())) {
			return null;
		}
		
		query.setSelection(selToken);
		
		OrderingToken orderByToken =
				MemoryBoot.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
			
		return connectivity.queryCursor(query);
	}

	@Override
	protected MemorySearchableContent memoryToSearchableContent(
			Cursor memoryCursor) {
		if (memoryCursor == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		MemoryBoot boot = new MemoryBoot(context);
		boot.fillValuesFromCursor(memoryCursor);

		MemorySearchableContent content = 
				new MemorySearchableContent();
		
		content.time = String.valueOf(boot.getTime());

		content.text1 = String.format("Boot %04d", boot.getBootSequence());

		content.text2 = DateTimePrintUtils.printTimeString(
				context, boot.getTime());

		content.icon1ResId = composeResourceUri(context, R.drawable.ic_boot);
		
		return content;
	}
	
	@Override
	protected String getSearchableAuthority() {
		return SEARCHABLE_AUTHORITY;
	}

}
