package com.dailystudio.memory.application.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.memory.application.FavoriteAppsSetting;
import com.dailystudio.memory.application.R;

public class FavoriteAppsSettingsAdapter extends ArrayAdapter<FavoriteAppsSetting> {

	public FavoriteAppsSettingsAdapter(Context context) {
		super(context, 0, new ArrayList<FavoriteAppsSetting>());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)  {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_favorite_apps_setting, null);
		}
		
		final Context context = getContext();
		if (context == null) {
			return convertView;
		}

		FavoriteAppsSetting setting = getItem(position);
		if (setting != null && convertView != null) {
			final Resources res = context.getResources();

			TextView labelView = (TextView) convertView.findViewById(R.id.res_label);
			if (labelView != null) {
				CharSequence label = context.getString(setting.labelResId);
				if (label == null) {
					if (res != null) {
						label = res.getString(R.string.error_unknow);
					}
				}
				
				labelView.setText(label);
			}
			
			ImageView iconView = (ImageView) convertView.findViewById(R.id.res_icon);
			if (iconView != null && res != null) {
				Drawable icon = res.getDrawable(setting.iconResId);
				if (icon == null) {
					if (res != null) {
						icon = res.getDrawable(R.drawable.ic_app_favorite);
					}
				}
				
				iconView.setImageDrawable(icon);
			}
		}
		
		return convertView;
	}

}
