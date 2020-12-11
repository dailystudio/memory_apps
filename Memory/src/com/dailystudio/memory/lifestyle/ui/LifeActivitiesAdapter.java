package com.dailystudio.memory.lifestyle.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LifeActivitiesAdapter extends ArrayAdapter<LifeActivity> {

	private final static String DEFAULT_TIME_FORMAT = "HH:mm";
	
	public LifeActivitiesAdapter(Context context) {
		super(context, 0, new ArrayList<LifeActivity>());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView instanceof ViewGroup == false) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.life_activity_item, null);
		}
		
		LifeActivity activity = getItem(position);

		if (activity != null && convertView instanceof ViewGroup) {
			final long sVal = activity.getStartTime();
			final long eVal = activity.getEndTime();
			
			String timeFormat = getContext().getString(
					R.string.time_print_simple_time_format_24);
			if (TextUtils.isEmpty(timeFormat)) {
				timeFormat = DEFAULT_TIME_FORMAT;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

			TextView sValView = (TextView) convertView.findViewById(
					R.id.activity_start_val);
			if (sValView != null) {
				sValView.setText(sdf.format(sVal));
			}

			TextView eValView = (TextView) convertView.findViewById(
					R.id.activity_end_val);
			if (eValView != null) {
				eValView.setText(sdf.format(eVal));
				if (sVal == eVal) {
					eValView.setVisibility(View.INVISIBLE);
				} else {
					eValView.setVisibility(View.VISIBLE);
				}
			}
			
			ImageView iv = (ImageView) convertView.findViewById(
					R.id.activity_icon);
			if (iv != null) {
				Drawable d = activity.getMainDrawable(getContext());
				Logger.debug("feature: %s, thumb: %s",
						activity, d);
				iv.setImageDrawable(d);
			}

			TextView titleView = (TextView) convertView.findViewById(
					R.id.activity_title);
			if (titleView != null) {
				titleView.setText(activity.getLabel(getContext()));
			}
		}

		return convertView;
	}

}
