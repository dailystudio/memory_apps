package com.dailystudio.memory.application.databaseobject;

import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;

public class FavoriteAppDatabaseModal {

	public static List<FavoriteApp> listTopFavoriteApps(Context context, 
			boolean incSysApps,
			int favoriteClass,
			int limit) {
		if (context == null) {
			return null;
		}
		
		final TimeCapsuleDatabaseReader<FavoriteApp> reader =
				new TimeCapsuleDatabaseReader<FavoriteApp>(context,
						FavoriteApp.class);

		Query query = new Query(FavoriteApp.class);
		
		ExpressionToken selToken = 
				FavoriteApp.COLUMN_FAVORTIE_CLASS.eq(favoriteClass);
		if (!incSysApps && selToken != null) {
			selToken = selToken.and(FavoriteApp.COLUMN_SYSTEM_APP.eq(0));
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		if (limit > 0) {
			ExpressionToken limitToken = new ExpressionToken(limit);
			
			query.setLimit(limitToken);
		}
					
		return reader.query(query);
	}

	public static void clearFavoriteApps(Context context, int favoriteClass) {
		if (context == null) {
			return;
		}
		
		final DatabaseConnectivity connectivity = 
				new DatabaseConnectivity(context, FavoriteApp.class);

		Query query = new Query(FavoriteApp.class);
		
		ExpressionToken selToken = 
				FavoriteApp.COLUMN_FAVORTIE_CLASS.eq(favoriteClass);
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		connectivity.delete(query);
	}

}
