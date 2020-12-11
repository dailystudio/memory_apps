package com.dailystudio.memory.mood;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.searchable.MemorySearchableContent;
import com.dailystudio.memory.searchable.MemorySearchableContentProvider;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.MemorySearchableSuggestion;
import com.dailystudio.memory.searchable.queryparams.TextQueryParameter;
import com.dailystudio.memory.searchable.queryparams.TimeQueryParameter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MoodSearchableContentProvider 
	extends MemorySearchableContentProvider {

	private final static String SEARCHABLE_AUTHORITY = 
			"com.dailystudio.memory.mood.searchable";
	
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
		
		suggestion.text1 = context.getString(R.string.category_mood_label);
		suggestion.text2 = String.format(
				context.getString(R.string.default_searchable_matches_templ), 
				c.getCount());
		
		suggestion.icon1ResId = composeResourceUri(context, R.drawable.ic_mood);
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
						MemoryMood.class);
		
		Query query = new Query(MemoryMood.class);
		
		ExpressionToken selToken = null;

		final List<TimeQueryParameter> timeParams = 
				searchQuery.timeQueryParams;
		final List<TextQueryParameter> textparams = 
				searchQuery.textQueryParams;
		
		ExpressionToken timeSelToken = null;
		ExpressionToken textSelToken = null;
		
		ExpressionToken subToken = null;
		for (TimeQueryParameter tqp: timeParams) {
			subToken = MemoryMood.COLUMN_TIME.gte(tqp.timeBegin)
					.and(MemoryMood.COLUMN_TIME.lte(tqp.timeEnd));
			if (timeSelToken == null) {
				timeSelToken = subToken;
			} else {
				timeSelToken = timeSelToken.or(subToken);
			}
		}
		
		List<Mood> moods = Moods.listMoods();
		List<Integer> moodIds = null;
		String label = null;
		for (TextQueryParameter tqp: textparams) {
			if (TextUtils.isEmpty(tqp.text)) {
				continue;
			}
			
			moodIds = new ArrayList<Integer>();
			
			for (Mood m: moods) {
				label = context.getString(m.labelResId);
				if (TextUtils.isEmpty(label)) {
					continue;
				}
				
				if (label.contains(tqp.text)) {
					moodIds.add(m.identifier);
				}
			}
			
			subToken = MemoryMood.COLUMN_COMMENTS.like(
					"%" + tqp.text + "%");
					
			if (moodIds.size() > 0) {
				subToken = subToken.or(
						MemoryMood.COLUMN_MOOD.inValues(
								moodIds.toArray(new Integer[0])));
			}
			
			if (textSelToken == null) {
				textSelToken = subToken;
			} else {
				textSelToken = textSelToken.or(subToken);
			}
		}
		
		if (timeSelToken != null) {
			selToken = timeSelToken;
		}
		
		if (textSelToken != null) {
			if (selToken == null) {
				selToken = textSelToken;
			} else {
				selToken = selToken.and(textSelToken);
			}
		}
		
		if (selToken == null
				|| TextUtils.isEmpty(selToken.toString())) {
			return null;
		}
		
		query.setSelection(selToken);
		
		OrderingToken orderByToken =
				MemoryMood.COLUMN_TIME.orderByDescending();
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
		
		final Resources res = context.getResources();
		if (res == null) {
			return null;
		}
		
		MemoryMood mm = new MemoryMood(context);
		mm.fillValuesFromCursor(memoryCursor);
		
		MemorySearchableContent content = 
				new MemorySearchableContent();
		
		final Mood mood = Moods.getMood(mm.getMood());
		if (mood == null) {
			return null;
		}
		
		final String label = context.getString(mood.labelResId);
		final String comment = mm.getComment();
		
		StringBuilder builder = null;
		
		builder = new StringBuilder();

		if (!TextUtils.isEmpty(label)) {
			builder.append(label);
		}
		
		if (!TextUtils.isEmpty(label) 
				&& !TextUtils.isEmpty(comment)) {
			builder.append(": ");
		}
		
		if (!TextUtils.isEmpty(comment)) {
			builder.append(comment);
		}
		
		content.time = String.valueOf(mm.getTime());
		content.text1 = builder.toString();

		content.text2 = DateTimePrintUtils.printTimeString(
				context, mm.getTime());
		
		content.icon1ResId = composeResourceUri(
				context, mood.iconResId);
		
		return content;
	}

	@Override
	protected String getSearchableAuthority() {
		return SEARCHABLE_AUTHORITY;
	}

}
