package com.dailystudio.memory.application.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.ui.AbsResObjectAdapter;

public class FavoriteAppsAdapter extends AbsResObjectAdapter<FavoriteApp> {

	public FavoriteAppsAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_favorite_app, null);
		}
		
		return convertView;
	}

}
