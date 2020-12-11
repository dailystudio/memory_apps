package com.dailystudio.memory.boot.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.memory.boot.MemoryScreenOn;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MemoryScreenOnAdapter extends SimpleDatabaseObjectCursorAdapter<MemoryScreenOn> {

	public MemoryScreenOnAdapter(Context context) {
		super(context, R.layout.screen_on_item, MemoryScreenOn.class);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		ImageView iconView = (ImageView) view.findViewById(R.id.memory_icon);
		if (iconView != null) {
			iconView.setImageResource(R.drawable.ic_screen_on);			
		}
		
		final MemoryScreenOn screenOn = dumpItem(c);
		if (screenOn == null) {
			return;
		}

		TextView tiemView = (TextView) view.findViewById(R.id.screen_on_time);
		if (tiemView != null) {
			tiemView.setText(DateTimePrintUtils.printTimeString(
					context, screenOn.getTime()));
		}
		
		TextView durationView = (TextView) view.findViewById(R.id.screen_on_duration);
		if (durationView != null) {
			durationView.setText(DateTimePrintUtils.printDurationString(
					context, screenOn.getDuration()));
		}
		
		TextView seqView = (TextView) view.findViewById(R.id.screen_on_boot_seq);
		if (seqView != null) {
			seqView.setText(String.format("Boot %04d", screenOn.getBootSequence()));
		}
	}
	
}
