package com.dailystudio.memory.mood.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.Moods;
import com.dailystudio.memory.mood.R;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class MemoryMoodAdapter extends SimpleDatabaseObjectCursorAdapter<MemoryMood> {

	public MemoryMoodAdapter(Context context) {
		super(context, R.layout.memory_mood, MemoryMood.class);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		MemoryMood mm = dumpItem(cursor);
		if (mm == null) {
			return;
		}
		
		TextView indView = (TextView) view.findViewById(R.id.memory_index);
		if (indView != null) {
			indView.setText(String.valueOf(cursor.getPosition()));
		}
		
		ImageView iconView = (ImageView) view.findViewById(R.id.memory_icon);
		if (iconView != null) {
			final Mood mood = Moods.getMood(mm.getMood());
			
			iconView.setImageResource(mood.iconResId);
		}
		
		TextView commentView = (TextView) view.findViewById(R.id.mood_comments);
		if (commentView != null) {
			commentView.setText(mm.getComment());
		}
		
		TextView timeView = (TextView) view.findViewById(R.id.mood_time);
		if (timeView != null) {
			timeView.setText(DateTimePrintUtils.printTimeStringWithoutDate(
					context, mm.getTime()));
		}
	}

}
