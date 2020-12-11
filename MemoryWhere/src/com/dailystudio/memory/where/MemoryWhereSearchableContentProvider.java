package com.dailystudio.memory.where;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.searchable.MemorySearchableContent;
import com.dailystudio.memory.searchable.MemorySearchableContentProvider;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.MemorySearchableSuggestion;
import com.dailystudio.memory.searchable.queryparams.TimeQueryParameter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public class MemoryWhereSearchableContentProvider 
	extends MemorySearchableContentProvider {

	private final static String SEARCHABLE_AUTHORITY = 
			"com.dailystudio.memory.where.searchable";

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
		
		suggestion.text1 = context.getString(R.string.category_where_label);
		suggestion.text2 = String.format(
				context.getString(R.string.default_searchable_matches_templ), 
				c.getCount());
		suggestion.icon1ResId = composeResourceUri(context, R.drawable.ic_where);
		
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
						IdspotHistory.class);
		
		Query query = new Query(IdspotHistory.class);
		
		final List<TimeQueryParameter> timeParams = 
				searchQuery.timeQueryParams;
		
		ExpressionToken selToken = null;
		
		ExpressionToken subToken = null;
		for (TimeQueryParameter tqp: timeParams) {
			subToken = IdspotHistory.COLUMN_TIME.gte(tqp.timeBegin)
					.and(IdspotHistory.COLUMN_TIME.lte(tqp.timeEnd));
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
				IdspotHistory.COLUMN_TIME.orderByDescending();
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
		
		IdspotHistory history = new IdspotHistory(context);
		history.fillValuesFromCursor(memoryCursor);
		
		final HotspotIdentity identity = history.getIdentity();
		if (identity == null) {
			return null;
		}
		
	    final HotspotIdentityInfo hiInfo = 
	            HotspotIdentifier.getIdentityInfo(identity);
		if (hiInfo == null) {
			return null;
		}
		
		final long duration = history.getDuration();
		
		long durVal = 0;
		String durstr = null;
		if (duration > CalendarUtils.HOUR_IN_MILLIS) {
			durVal = Math.round((history.getDuration() / CalendarUtils.HOUR_IN_MILLIS));
			durstr = context.getString(R.string.search_duration_hour_templ, 
					context.getString(hiInfo.labelResId),
					durVal);
		} else {
			durVal = Math.round((history.getDuration() / CalendarUtils.MINUTE_IN_MILLIS));
			durstr = context.getString(R.string.search_duration_min_templ, 
					context.getString(hiInfo.labelResId),
					durVal);
		}
		

		MemorySearchableContent content = 
				new MemorySearchableContent();
		
		content.time = String.valueOf(history.getTime());

		content.text1 = durstr;

		content.text2 = DateTimePrintUtils.printTimeString(
				context, history.getTime());

		content.icon1ResId = composeResourceUri(context, hiInfo.iconResId);
		
		return content;
	}
	
	@Override
	protected String getSearchableAuthority() {
		return SEARCHABLE_AUTHORITY;
	}

}
