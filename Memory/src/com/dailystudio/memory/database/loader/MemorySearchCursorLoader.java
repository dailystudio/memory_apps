package com.dailystudio.memory.database.loader;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.plugin.PluginManager;
import com.dailystudio.memory.searchable.MemorySearchableContent;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.TimeSortedCursor;
import com.google.gson.Gson;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

public class MemorySearchCursorLoader extends CursorLoader {
	
    final ForceLoadContentObserver mObserver;

    private WeakReference<ActionBarActivity> mHostActivityRef;
    private String mQueryInput;
    private String mQueryAuthority;
    
	public MemorySearchCursorLoader(Context context, 
			String queryInput,
			String queryAuthroity) {
		super(context);
		
		if (context instanceof ActionBarActivity) {
			mHostActivityRef = new WeakReference<ActionBarActivity>(
					(ActionBarActivity)context);
		}
		
		mQueryInput = queryInput;
		mQueryAuthority = queryAuthroity;
		
		mObserver = new ForceLoadContentObserver();
	}
	
	@Override
	public Cursor loadInBackground() {
		showPrompt(R.string.prompt_search);
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		final MemorySearchableQuery query = 
				buildQuery();
		if (query == null) {
			return null;
		}
		
        MatrixCursor cursor = new MatrixCursor(MemorySearchableContent.COLUMNS);
        
    	fillPluginsContent(cursor, query);

        Cursor c = new TimeSortedCursor(cursor, 
        		SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);
        
        hidePrompt();
        
        return c;
	}

	private MemorySearchableQuery buildQuery() {
    	if (TextUtils.isEmpty(mQueryInput)) {
    		return null;
    	}
    	
        String inputs = mQueryInput;
    	Logger.debug("inputs = %s", inputs);
    	if (inputs == null || inputs.length() == 0) {
            return null;
        }
    	
    	MemorySearchableQuery query = analyzeInputs(inputs);
    	if (query == null) {
    		return null;
    	}
    	
    	return query;
	}
	
    private MemorySearchableQuery analyzeInputs(String inputs) {
    	final MemorySearchableQuery query = 
    			new MemorySearchableQuery();
    	
    	query.buildFromInputs(inputs);
    	
    	return query;
    }

	private void fillPluginsContent(MatrixCursor outCurosr, 
			MemorySearchableQuery query) {
    	if (outCurosr == null || query == null) {
    		return;
    	}
    	
    	final Context context = getContext();
    	if (context == null) {
    		return;
    	}
    	
    	Gson gson = new Gson();
    	
    	String querystr = gson.toJson(query);
    	if (querystr == null) {
    		return;
    	}
    		
    	List<MemoryPluginInfo> pInfos = PluginManager.listPlugins();
    	if (pInfos == null) {
    		return;
    	}
    	
    	String seachableAuthority = null;
    	Cursor c = null;
    	MemorySearchableContent content = null;
    	int index = 0;
    	Map<String, Cursor> cursors = new HashMap<String, Cursor>();
    	
    	for (MemoryPluginInfo pInfo: pInfos) {
    		seachableAuthority = pInfo.getSearchableAuthority();
    		if (seachableAuthority == null) {
    			continue;
    		}
    		
    		if (mQueryAuthority != null
    				&& !mQueryAuthority.equals(seachableAuthority)) {
    			continue;
    		}
    		
    		final Uri queryUri = Uri.parse("content://" + 
    				seachableAuthority + "/" + 
    				Constants.SEARCH_SEGMENT_DATA);
    		
    		c = getContext().getContentResolver().query(
    				queryUri, null, null, new String[] { querystr }, null);
    		
//    		Logger.debug("seachableAuthority = %s, querystr = %s, c = %s", 
//    				seachableAuthority,
//    				querystr, c);
//    		DatabaseUtils.dumpCursor(c);
    		
    		if (c == null) {
    			continue;
    		}

    		cursors.put(pInfo.getLabel(), c);
    	}
   	
    	int N = cursors.size();
    	Logger.debug("N = %d", N);
    	
    	if (N <= 0) {
        	updateProgress(1.f);

    		return;
    	}
    	
    	Set<String> pnames = cursors.keySet();

    	float percent = .2f;
    	
    	updateProgress(percent);
    	
    	int count = 0;
    	for (String pname: pnames) {
    		c = cursors.get(pname);
    		if (c == null) {
    			continue;
    		}
    		
    		count += cursors.get(pname).getCount();
    	}
    	
    	Logger.debug("count = %d", count);
    	
    	if (count <= 0) {
        	updateProgress(1.f);

    		return;
    	}

    	final float pdelta = (1.f - percent) / count;
    	float pinc = 0;
    	for (String pname: pnames) {
    		c = cursors.get(pname);
    		if (c == null) {
    			continue;
    		}
    		
     		showPrompt(context.getString(R.string.prompt_search_detail,
    				pname));
    		try {
    			if (c.moveToFirst()) {
    				do {
    					content = MemorySearchableContent.parseFromCursor(c);
    					if (content != null) {
    						content.id = index++;
    						outCurosr.addRow(content.toColumnValues());
    					}
    					
    					pinc += pdelta;
    					if (pinc >= .1f) {
    						percent += pinc;
    						
    						updateProgress(percent);
	    					
	    					pinc = 0.f;
    					}
    				} while (c.moveToNext());
    			}
    		} finally {
    			
    		}

    		if (c != null) {
    			c.close();
    		}
    		
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	updateProgress(1.f);
    }

	private void updateProgress(float percent) {
//    	Logger.debug("percent = %f", percent);
		final ActionBarActivity activity = getHostActivity();
		if (activity == null) {
			return;
		}
		
		int progress = (int)Math.round(percent * 100);
		activity.setActionBarProgress(progress);
	}
	
	private ActionBarActivity getHostActivity() {
		if (mHostActivityRef == null) {
			return null;
		}
		
		return mHostActivityRef.get();
	}
	
	private void showPrompt(int resId) {
		final String prompt = getContext().getString(resId);
		
		showPrompt(prompt);
	}
	
	private void showPrompt(CharSequence prompt) {
		final ActionBarActivity activity = getHostActivity();
		if (activity == null) {
			return;
		}
		
		activity.showPrompt(prompt);
	}
	
	private void hidePrompt() {
		final ActionBarActivity activity = getHostActivity();
		if (activity == null) {
			return;
		}
		
		activity.hidePrompt();
	}


}
