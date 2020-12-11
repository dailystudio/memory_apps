package com.dailystudio.memory.mood;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.querypiece.MemoryPieceCard;
import com.dailystudio.memory.querypiece.MemoryPieceDigest;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;
import com.dailystudio.memory.querypiece.MemoryPieceQueryService;

public class MemoryMoodPieceQueryService extends MemoryPieceQueryService {

	private final static String SERVICE_NAME = 
			"memory-mood-piece-query-service";
	
	public MemoryMoodPieceQueryService() {
		super(SERVICE_NAME);
	}

	@Override
	protected long onQueryMemoryPiceceCount(Context context, String queryArgs) {
		if (context == null) {
			return 0l;
		}
		
		TimeCapsuleDatabaseReader<MemoryMood> reader =
			new TimeCapsuleDatabaseReader<MemoryMood>(context, MemoryMood.class);

		Query query = new Query(MemoryMood.class);
		
		final long count = reader.queryCount(query);
		Logger.debug("count = %d", count);
		
		return count;
	}

	@Override
	protected List<MemoryPieceDigest> onQueryMemoryPiceceDigests(
			Context context, String queryArgs) {
		Logger.debug("queryArgs = %s", queryArgs);
		if (TextUtils.isEmpty(queryArgs)) {
			return null;
		}
		
		List<MemoryPieceDigest> digests = new ArrayList<MemoryPieceDigest>();
		if (queryArgs.startsWith(MemoryPieceKeyNotes.ARG_KEY_NOTES)) {
			long[] timespan = MemoryPieceKeyNotes.extractTimeSpan(queryArgs);
			
			TimeCapsuleDatabaseReader<MemoryMood> reader =
					new TimeCapsuleDatabaseReader<MemoryMood>(context, MemoryMood.class);

			fillMoods(context, reader.query(timespan[0], timespan[1]),
					digests);
		}
		
		return digests;
	}

	@Override
	protected List<MemoryPieceCard> onQueryMemoryPiceceCards(Context context,
			String queryArgs) {
		return null;
	}
	
	private void fillMoods(Context context, 
			List<MemoryMood> moods,
			List<MemoryPieceDigest> digests) {
		if (context == null
				|| moods == null || moods.size() <= 0 
				|| digests == null) {
			return;
		}
		
		MemoryPieceDigest digest = null;
		String content = null;
		for (MemoryMood m: moods) {
			content = composeContent(context, m);
			
			digest = new MemoryPieceDigest(content);
			digest.setTimestamp(m.getTime());
			digest.setPluginComponent(new ComponentName(
					context.getApplicationContext(),
					PluginMood.class));
			
			digests.add(digest);
		}
	}
	
	private String composeContent(Context context, MemoryMood mood) {
		if (mood == null) {
			return null;
		}
		
		final String comment = mood.getComment();
		
		final int mid = mood.getMood();
		final Mood m = Moods.getMood(mid);
		if (m == null) {
			return comment;
		}
		
		if (TextUtils.isEmpty(comment)) {
			return context.getString(m.digestResId);
		}
		
		return String.format("[%s]: %s",
				context.getString(m.labelResId),
				comment);
	}

}
