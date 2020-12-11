package com.dailystudio.memory.where.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

public class MemoryLocationAdapter extends SimpleDatabaseObjectCursorAdapter<MemoryLocation> {

	public MemoryLocationAdapter(Context context) {
		super(context, R.layout.location_item, MemoryLocation.class);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		final MemoryLocation location = dumpItem(c);
		if (location == null) {
			return;
		}
		
		TextView latview = (TextView) view.findViewById(R.id.geo_lat);
		if (latview != null) {
			latview.setText(String.format("%.9f", location.getLatitude()));
		}

		TextView lonview = (TextView) view.findViewById(R.id.geo_lon);
		if (lonview != null) {
			lonview.setText(String.format("%.9f", location.getLongitude()));
		}

		TextView timeView = (TextView) view.findViewById(R.id.location_time);
		if (timeView != null) {
			final long time = location.getTime();
			
			timeView.setText(DateTimePrintUtils.printTimeString(mContext, time));
		}

		TextView durView = (TextView) view.findViewById(R.id.location_duration);
		if (durView != null) {
			final long duration = location.getDuration();
			
			durView.setText(DateTimePrintUtils.printDurationString(mContext, duration));
		}

	}
	
}
