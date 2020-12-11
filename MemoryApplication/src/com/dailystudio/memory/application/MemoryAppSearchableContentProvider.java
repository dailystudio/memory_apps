package com.dailystudio.memory.application;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.text.TextUtils;
import android.util.SparseArray;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.databaseobject.UsageComponent;
import com.dailystudio.memory.application.databaseobject.UsageComponentDatabaseModal;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.searchable.MemorySearchableContent;
import com.dailystudio.memory.searchable.MemorySearchableContentProvider;
import com.dailystudio.memory.searchable.MemorySearchableQuery;
import com.dailystudio.memory.searchable.MemorySearchableSuggestion;
import com.dailystudio.memory.searchable.queryparams.TextQueryParameter;
import com.dailystudio.memory.searchable.queryparams.TimeQueryParameter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MemoryAppSearchableContentProvider 
	extends MemorySearchableContentProvider {

	private final static String SEARCHABLE_AUTHORITY = 
			"com.dailystudio.memory.app.searchable";
	
	private final static long USAGE_DURATION_THRESHOLD = 
			(CalendarUtils.MINUTE_IN_MILLIS * 5);

	private SparseArray<String> mCompIdsMap =
			new SparseArray<String>();
	
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
		
		suggestion.text1 = context.getString(R.string.category_app_label);
		suggestion.text2 = String.format(
				context.getString(R.string.default_searchable_matches_templ), 
				c.getCount());
		suggestion.icon1ResId = composeResourceUri(context, R.drawable.ic_app);

		suggestion.intentExtraData = String.valueOf(c.getCount());
		
		suggestions.add(suggestion);
		
		c.close();
		
		return suggestions;
	}

	@Override
	protected Cursor doQueryData(MemorySearchableQuery searchQuery) {
		Cursor c1 = doQueryAppInstHistory(searchQuery);
		Logger.debug("c1 = %s, count = %d", c1, (c1 == null ? 0 : c1.getCount()));
		Cursor c2 = doQueryAppUsage(searchQuery);
		Logger.debug("c2 = %s, count = %d", c2, (c2 == null ? 0 : c2.getCount()));
		
//		DatabaseUtils.dumpCursor(c1);
//		DatabaseUtils.dumpCursor(c2);
		
		MergeCursor c = new MergeCursor(new Cursor[] {c1, c2});
//		DatabaseUtils.dumpCursor(c);
		Logger.debug("c1 = %s[count = %d], c2 = %s[count = %d], c = %s[count = %d]",
				c1, (c1 == null ? 0 : c1.getCount()),
				c2, (c2 == null ? 0 : c2.getCount()),
				c, (c == null ? 0 : c.getCount()));
		
		if (c.getCount() > 0) {
			prepareCompIdsMap();
		}

		return c;
	}

	private void prepareCompIdsMap() {
		if (mCompIdsMap == null) {
			return;
		}
		
		final Context context = getContext();
		
		mCompIdsMap.clear();
		
		List<UsageComponent> components = 
				UsageComponentDatabaseModal.listUsageComponents(context);
		if (components == null) {
			return;
		}
	
		for (UsageComponent comp: components) {
			mCompIdsMap.put(comp.getId(), comp.getPackageName());
		}
	}
	
	private Cursor doQueryAppInstHistory(MemorySearchableQuery searchQuery) {
		if (searchQuery == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}

		DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(getContext(),
						AppInstHistory.class);
		
		Query query = new Query(AppInstHistory.class);
		
		ExpressionToken selToken = null;

		final List<TimeQueryParameter> timeParams = 
				searchQuery.timeQueryParams;
		final List<TextQueryParameter> textparams = 
				searchQuery.textQueryParams;
		
		ExpressionToken timeSelToken = null;
		ExpressionToken textSelToken = null;
		
		ExpressionToken subToken = null;
		for (TimeQueryParameter tqp: timeParams) {
			subToken = AppInstHistory.COLUMN_TIME.gte(tqp.timeBegin)
					.and(AppInstHistory.COLUMN_TIME.lte(tqp.timeEnd));
			if (timeSelToken == null) {
				timeSelToken = subToken;
			} else {
				timeSelToken = timeSelToken.or(subToken);
			}
		}
		
		for (TextQueryParameter tqp: textparams) {
			if (TextUtils.isEmpty(tqp.text)) {
				continue;
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
				AppInstHistory.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		return connectivity.queryCursor(query);
	}
	
	private Cursor doQueryAppUsage(MemorySearchableQuery searchQuery) {
		if (searchQuery == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}

		DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(getContext(),
						Usage.class);
		
		Query query = new Query(Usage.class);
		
		ExpressionToken selToken = null;

		final List<TimeQueryParameter> timeParams = 
				searchQuery.timeQueryParams;
		final List<TextQueryParameter> textparams = 
				searchQuery.textQueryParams;
		
		ExpressionToken timeSelToken = null;
		ExpressionToken textSelToken = null;
		
		ExpressionToken subToken = null;
		for (TimeQueryParameter tqp: timeParams) {
			subToken = Usage.COLUMN_TIME.gte(tqp.timeBegin)
					.and(Usage.COLUMN_TIME.lte(tqp.timeEnd));
			if (timeSelToken == null) {
				timeSelToken = subToken;
			} else {
				timeSelToken = timeSelToken.or(subToken);
			}
		}
		
		for (TextQueryParameter tqp: textparams) {
			if (TextUtils.isEmpty(tqp.text)) {
				continue;
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
		
		query.setSelection(selToken.and(
				Usage.COLUMN_DURATION.gt(USAGE_DURATION_THRESHOLD)));
		
		OrderingToken orderByToken =
				Usage.COLUMN_TIME.orderByDescending();
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
		
		MemorySearchableContent content = null;
		if (memoryCursor.getColumnIndex(AppInstHistory.COLUMN_PACKAGE_ACTION.getName()) != -1) {
			content = appInstHistory(memoryCursor);
		} else {
			content = appUsageHistory(memoryCursor);
		}
		
		return content;
	}
	
	private MemorySearchableContent appInstHistory(Cursor memoryCursor) {
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return null;
		}
		
		final AppInstHistory history = new AppInstHistory(context);
		history.fillValuesFromCursor(memoryCursor);
		
		final ApplicationInfo aInfo = dumpAppInfo(
				context, history.getPackageName());
		
		CharSequence label = dumpLabel(context, aInfo);
		if (label == null 
				|| label.equals(history.getPackageName())) {
			label = history.getCachedLabel();
		}
		
		final int iconResId = dumpIconResId(context, aInfo);
		
		final String action = history.getPackageAction();
		
		MemorySearchableContent content = 
				new MemorySearchableContent();
		
		StringBuilder builder = null;
		
		builder = new StringBuilder();

		if (!TextUtils.isEmpty(label)) {
			builder.append(label);
		}
		
//		if (!TextUtils.isEmpty(label) 
//				&& !TextUtils.isEmpty(action)) {
//			builder.append(": ");
//		}
//		
//		if (!TextUtils.isEmpty(action)) {
//			builder.append(action);
//		}
		
		content.time = String.valueOf(history.getTime());
		
		content.text1 = builder.toString();

		content.text2 = DateTimePrintUtils.printTimeString(
				context, history.getTime());
		
		content.icon1ResId = composeResourceUri(
				context, AppInstHistory.getAciontSearchIcon(action));
		
		if (iconResId > 0) {
			content.icon2ResId = composeResourceUri(
					context, history.getPackageName(), iconResId);
		} else {
			content.icon2ResId = composeIconFileUri(
					context, history.getIconCachePath());
		}
		
		return content;
	}

	private MemorySearchableContent appUsageHistory(Cursor memoryCursor) {
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return null;
		}
		
		final Usage usage = new Usage(context);
		usage.fillValuesFromCursor(memoryCursor);
		
		final String pkgName = mCompIdsMap.get(usage.getComponentId());
		if (pkgName == null) {
			return null;
		}
		
		final ApplicationInfo aInfo = dumpAppInfo(
				context, pkgName);
		
		CharSequence label = dumpLabel(context, aInfo);
		if (label == null 
				|| label.equals(pkgName)) {
			label = usage.getCachedLabel();
		}
		
		final int iconResId = dumpIconResId(context, aInfo);
		
		MemorySearchableContent content = 
				new MemorySearchableContent();
		
		final long duration = usage.getDuration();
		
		long durVal = 0;
		String durstr = null;
		if (duration > CalendarUtils.HOUR_IN_MILLIS) {
			durVal = Math.round((usage.getDuration() / CalendarUtils.HOUR_IN_MILLIS));
			durstr = context.getString(R.string.search_usage_duration_hour_templ, 
					durVal, label);
		} else {
			durVal = Math.round((usage.getDuration() / CalendarUtils.MINUTE_IN_MILLIS));
			durstr = context.getString(R.string.search_usage_duration_min_templ, 
					durVal, label);
		}
		
		content.time = String.valueOf(usage.getTime());
		
		content.text1 = durstr;

		content.text2 = DateTimePrintUtils.printTimeString(
				context, usage.getTime());
		
		content.icon1ResId = composeResourceUri(
				context, R.drawable.ic_app_run_pressed);
		if (iconResId > 0) {
			content.icon2ResId = composeResourceUri(
					context, pkgName, iconResId);
		} else {
			content.icon2ResId = composeIconFileUri(
					context, usage.getIconCachePath());
		}
		
		return content;
	}
	
	private ApplicationInfo dumpAppInfo(Context context, String pkgName) {
		if (context == null || pkgName == null) {
			return null;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return null;
		}
		
		ApplicationInfo aInfo = null;
		try {
			aInfo = pkgmgr.getApplicationInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			Logger.debug("dump label failure for app[%s]: %s",
					pkgName, e.toString());
			
			aInfo = null;
		}
		
		return aInfo;
	}
	
	private CharSequence dumpLabel(Context context, 
			ApplicationInfo aInfo) {
		if (context == null || aInfo == null) {
			return null;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return null;
		}
		
		return aInfo.loadLabel(pkgmgr);
	}
	
	private int dumpIconResId(Context context,
			ApplicationInfo aInfo) {
		if (context == null || aInfo == null) {
			return -1;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return -1;
		}
		
		return aInfo.icon;
	}
	
    public static String composeIconFileUri(Context context, String filename) {
    	if (context == null || filename == null) {
    		return null;
    	}
    	
		StringBuilder builder = new StringBuilder();
		
		builder.append(ContentResolver.SCHEME_FILE);
		builder.append("://");
		builder.append(filename);
		
		return builder.toString();
    }

	@Override
	protected String getSearchableAuthority() {
		return SEARCHABLE_AUTHORITY;
	}

}
