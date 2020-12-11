package com.dailystudio.memory.where.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.Hotspot;

public class HotspotAdapter extends ArrayAdapter<Hotspot> {
	
	public HotspotAdapter(Context context) {
		super(context, 0, new ArrayList<Hotspot>());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = getContext();
		
		if (convertView == null)  {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.hotspot_item, null);
		}
		
		final Hotspot hotspot = getItem(position);
		if (hotspot != null && convertView != null) {
			
			TextView latview = (TextView) convertView.findViewById(R.id.geo_lat);
			if (latview != null) {
				latview.setText(String.format("%.9f", hotspot.getLatitude()));
			}
	
			TextView lonview = (TextView) convertView.findViewById(R.id.geo_lon);
			if (lonview != null) {
				lonview.setText(String.format("%.9f", hotspot.getLongitude()));
			}
	
			TextView occurr = (TextView) convertView.findViewById(R.id.hotspot_occurr);
			if (occurr != null) {
				final int occurrence = hotspot.getOccurrence();
				
				String timesLabel = context.getResources().getQuantityString(
						R.plurals.hotspot_occur, occurrence);
				occurr.setText(String.format("%d %s",
						occurrence, timesLabel));
			}
	
			TextView duration = (TextView) convertView.findViewById(R.id.hotspot_duration);
			if (duration != null) {
				duration.setText(DateTimePrintUtils.printDurationString(
						context, hotspot.getDuration()));
			}
		}
		
		return convertView;
	}
	
}
