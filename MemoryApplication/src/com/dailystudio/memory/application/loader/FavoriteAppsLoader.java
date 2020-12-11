package com.dailystudio.memory.application.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.dataobject.loader.DatabaseObjectsLoader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;

public class FavoriteAppsLoader extends DatabaseObjectsLoader<FavoriteApp> {
	
	private boolean mIncludeSysApps = false;
	private int mFavoriteClass = Constants.FAVORITE_CLASS_WEEK;
	
	public FavoriteAppsLoader(Context context) {
		super(context);
	}

	public FavoriteAppsLoader(Context context,
			boolean incSysApps, int favorite) {
		super(context);
		
		mIncludeSysApps = incSysApps;
		mFavoriteClass = favorite;
	}

	@Override
	public List<FavoriteApp> loadInBackground() {
		Logger.debug("incSysApps = %s, favorite = %d",
				mIncludeSysApps, mFavoriteClass);
		
		return super.loadInBackground();
	}

	@Override
	protected Class<FavoriteApp> getObjectClass() {
		return FavoriteApp.class;
	}
	
	@Override
	protected Query getQuery(Class<FavoriteApp> klass) {
		Query query = new Query(FavoriteApp.class);
		
		ExpressionToken selToken = 
				FavoriteApp.COLUMN_FAVORTIE_CLASS.eq(mFavoriteClass);
		if (!mIncludeSysApps && selToken != null) {
			selToken = selToken.and(FavoriteApp.COLUMN_SYSTEM_APP.eq(0));
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		ExpressionToken limitToken = new ExpressionToken(Constants.FAVORITE_APPS_LIMIT);
			
		query.setLimit(limitToken);
		
		return query;
	}

}
