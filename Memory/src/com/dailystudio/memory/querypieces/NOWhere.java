package com.dailystudio.memory.querypieces;

import java.util.List;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceDigestQuery;

public class NOWhere extends MemoryPieceDigestQuery<String> {
	
	public static final String HOTSPOT_HOME = "HOTSPOT_HOME";
	public static final String HOTSPOT_WORKPLACE = "HOTSPOT_WORKPLACE";
	
	private static final String ARG_NOWHERE = "now-where";
	
	private Context mContext;
	
	public NOWhere(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public String getLocationName() {
		return queryDigests(mContext, ARG_NOWHERE);
	}

	@Override
	protected String parseResults(Context context,
			List<MemoryPieceDigest> pieces) {
		if (pieces == null || pieces.size() <= 0) {
			return null;
		}
		
		final MemoryPieceDigest nowhere = pieces.get(0);
		Logger.debug("nowhere digest = %s", nowhere);
		
		if (nowhere == null) {
			return null;
		}
		
		return nowhere.getContent();
	}
	
}
