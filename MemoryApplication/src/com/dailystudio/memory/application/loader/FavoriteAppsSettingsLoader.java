package com.dailystudio.memory.application.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.FavoriteAppsSetting;
import com.dailystudio.memory.application.R;

public class FavoriteAppsSettingsLoader extends AbsAsyncDataLoader<List<FavoriteAppsSetting>> {

	public FavoriteAppsSettingsLoader(Context context) {
		super(context);
	}

	@Override
	public List<FavoriteAppsSetting> loadInBackground() {
		List<FavoriteAppsSetting> settings = 
				new ArrayList<FavoriteAppsSetting>();
		FavoriteAppsSetting setting = null;
		
		setting = new FavoriteAppsSetting(false, Constants.FAVORITE_CLASS_WEEK, 
				R.string.favorite_apps_week_user, R.drawable.ic_app_favorite,
				R.string.disp_label_favorite_apps_week);
		settings.add(setting);
		
		setting = new FavoriteAppsSetting(true, Constants.FAVORITE_CLASS_WEEK, 
				R.string.favorite_apps_week_all, R.drawable.ic_app_favorite,
				R.string.disp_label_favorite_apps_week);
		settings.add(setting);
		
		setting = new FavoriteAppsSetting(false, Constants.FAVORITE_CLASS_MONTH, 
				R.string.favorite_apps_month_user, R.drawable.ic_app_favorite,
				R.string.disp_label_favorite_apps_month);
		settings.add(setting);
		
		setting = new FavoriteAppsSetting(true, Constants.FAVORITE_CLASS_MONTH, 
				R.string.favorite_apps_month_all, R.drawable.ic_app_favorite,
				R.string.disp_label_favorite_apps_month);
		settings.add(setting);
		
		return settings;
	}
	

}
