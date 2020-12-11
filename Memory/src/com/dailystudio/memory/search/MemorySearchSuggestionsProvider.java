package com.dailystudio.memory.search;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.plugin.PluginManager;
import com.dailystudio.memory.searchable.MemorySearchableContentProvider;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.MemorySearchableSuggestion;
import com.dailystudio.memory.searchable.queryparams.KeywordQueryParameter;
import com.dailystudio.memory.searchable.queryparams.keywords.BuildinKeywords;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class MemorySearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
	
    public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public MemorySearchSuggestionsProvider() {
        setupSuggestions(Constants.MEMORY_SEARCH_AUTHORITY, MODE);
    }
    
	@Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
    	if (selectionArgs == null || selectionArgs.length <= 0) {
    		return null;
    	}
    	
        String inputs = selectionArgs[0];
    	Logger.debug("inputs = %s", inputs);
    	if (inputs == null || inputs.length() == 0) {
            return null;
        }
    	
    	MemorySearchableQuery query = analyzeInputs(inputs);
    	if (query == null) {
    		return null;
    	}
    	
        MatrixCursor cursor = new MatrixCursor(MemorySearchableSuggestion.COLUMNS);
        
        fillBuildinKeywordsSuggestions(cursor, query);
    	fillPluginsSuggestions(cursor, query);

        return cursor;
    }

    private void fillBuildinKeywordsSuggestions(MatrixCursor cursor,
			MemorySearchableQuery query) {
		if (cursor == null || query == null) {
			return;
		}
		
		if (query.keywordQueryParams == null) {
			return;
		}
		
		final Context context = getContext();
		if (context == null) {
			return;
		}
		
		String[] matchedKeywords = null;
		MemorySearchableSuggestion suggestion = null;
		for (KeywordQueryParameter kqp: query.keywordQueryParams) {
			matchedKeywords = BuildinKeywords.matchedKeywords(kqp.keyword);
			if (matchedKeywords == null) {
				continue;
			}
			
			for (String keyword: matchedKeywords) {
				suggestion = new MemorySearchableSuggestion();
				
				suggestion.text1 = keyword;
				suggestion.query = "@" + keyword;
				suggestion.icon1ResId = 
						MemorySearchableContentProvider.composeResourceUri(
								context, R.drawable.ic_search_memory);
				
				cursor.addRow(suggestion.toColumnValues());
			}
		}
	}

    private MemorySearchableQuery analyzeInputs(String inputs) {
    	final MemorySearchableQuery query = 
    			new MemorySearchableQuery();
    	
    	query.buildFromInputs(inputs);
    	
    	return query;
    }
    
	private void fillPluginsSuggestions(MatrixCursor outCurosr, MemorySearchableQuery query) {
    	if (outCurosr == null || query == null) {
    		return;
    	}
    	
    	Gson gson = new Gson();
    	
    	String querystr = gson.toJson(query);
    	if (querystr == null) {
    		return;
    	}
    	
    	final Context context = getContext();
    		
    	List<MemoryPluginInfo> pInfos = PluginManager.listPlugins();
    	if (pInfos == null) {
    		return;
    	}
    	
    	List<MemorySearchableSuggestion> toAdded = 
    			new ArrayList<MemorySearchableSuggestion>();
    	
    	String seachableAuthority = null;
    	Cursor c = null;
    	MemorySearchableSuggestion suggestion = null;
    	int index = 0;
    	int sumCount = 0;
    	int subCount = 0;
    	for (MemoryPluginInfo pInfo: pInfos) {
    		seachableAuthority = pInfo.getSearchableAuthority();
    		if (seachableAuthority == null) {
    			continue;
    		}
    		
    		final Uri queryUri = Uri.parse("content://" + 
    				seachableAuthority + "/" + 
    				Constants.SEARCH_SEGMENT_SUGGESTION);
    		
    		c = getContext().getContentResolver().query(
    				queryUri, null, null, new String[] { querystr }, null);
//    		Logger.debug("seachableAuthority = %s, querystr = %s, c = %s", 
//    				seachableAuthority,
//    				querystr, c);
//    		DatabaseUtils.dumpCursor(c);
    		
    		if (c == null) {
    			continue;
    		}
    		
    		try {
    			if (c.moveToFirst()) {
    				do {
    					suggestion = MemorySearchableSuggestion.parseFromCursor(c);
    					if (suggestion != null) {
    						suggestion.id = index++;
    						
    			    		subCount = 0;
    						if (suggestion.intentExtraData != null) {
    							try {
    								subCount = Integer.parseInt(
    										suggestion.intentExtraData);
    							} catch (NumberFormatException e) {
    								Logger.debug("parse subcount failure: %s", 
    										e.toString());
    								subCount = 0;
    							}
    						}
    						
    						sumCount += subCount;
    						
    						toAdded.add(suggestion);
//    						outCurosr.addRow(suggestion.toColumnValues());
    					}
    				} while (c.moveToNext());
    			}
    		} finally {
    			
    		}

    		if (c != null) {
    			c.close();
    		}
    	}
   	
    	if (sumCount > 0) {
    		suggestion = new MemorySearchableSuggestion();
    		
			suggestion.text1 = context.getString(
					R.string.memory_search_category_all);
			suggestion.text2 = String.format(
					context.getString(R.string.default_searchable_matches_templ), 
					sumCount);
			
			suggestion.query = query.queryInputs;
			suggestion.icon1ResId = 
					MemorySearchableContentProvider.composeResourceUri(
							context, R.drawable.ic_search_memory);

			toAdded.add(0, suggestion);
//			outCurosr.addRow(suggestion.toColumnValues());
    	}
    	
    	if (toAdded != null && toAdded.size() > 0) {
    		for (MemorySearchableSuggestion mss: toAdded) {
    			outCurosr.addRow(mss.toColumnValues());
    		}
    	}
    	
    }

}