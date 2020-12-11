package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.ui.AbsResObjectAdapter;

public class FavoriteAppsListAdapter extends AbsResObjectAdapter<FavoriteApp> {

	public FavoriteAppsListAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_fapp_list_item, null);
		}
		
		return convertView;
	}

}
