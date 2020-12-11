package com.dailystudio.memory.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.R;
import com.dailystudio.nativelib.application.AndroidActivity;

public class MainPageShortcutsAdapter extends AbsResObjectAdapter<AndroidActivity> {

	public MainPageShortcutsAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_mp_shortcut, null);
		}
		
		return convertView;
	}
	
}
