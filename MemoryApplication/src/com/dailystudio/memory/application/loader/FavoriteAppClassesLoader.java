package com.dailystudio.memory.application.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;

public class FavoriteAppClassesLoader extends AbsAsyncDataLoader<List<Integer>> {
	
	private final static int[] FAVORITE_CLASSES = {
		Constants.FAVORITE_CLASS_WEEK,
		Constants.FAVORITE_CLASS_MONTH,
		Constants.FAVORITE_CLASS_DAY_0_8,
		Constants.FAVORITE_CLASS_DAY_9_12,
		Constants.FAVORITE_CLASS_DAY_13_18,
		Constants.FAVORITE_CLASS_DAY_19_23,
	};

	public FavoriteAppClassesLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<Integer> loadInBackground() {
		
		List<Integer> classes = new ArrayList<Integer>();
		
		for (int i = 0; i < FAVORITE_CLASSES.length; i++) {
			classes.add(FAVORITE_CLASSES[i]);
			Logger.debug("fclass = %s", FavoriteApp.favoriteClassToString(
					FAVORITE_CLASSES[i]));
		}
		
		return classes;
	}

}
