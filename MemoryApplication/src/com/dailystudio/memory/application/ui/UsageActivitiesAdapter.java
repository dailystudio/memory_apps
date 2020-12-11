package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.AbsResObjectAdapter;
import com.dailystudio.nativelib.application.AndroidApplication;

public class UsageActivitiesAdapter extends AbsResObjectAdapter<AndroidApplication> {

	public UsageActivitiesAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.usage_activity_list_item, null);
		}
		
		return convertView;
	}
	
}
