package com.dailystudio.memory.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.querypiece.MemoryKeyNote;
import com.dailystudio.memory.querypiece.MemoryPieceKeyNotes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeynotesAdapter extends ArrayAdapter<MemoryKeyNote> {

	private final static String KEY_NOTE_MAIN_STYLE_START = "<font color=\"#9D3D35\">";
	private final static String KEY_NOTE_SUB_STYLE_START = "<font color=\"\"><b><i>";
	private final static String KEY_NOTE_MAIN_STYLE_END = "</font>";
	private final static String KEY_NOTE_SUB_STYLE_END = "</b></i></font>";
	
	public KeynotesAdapter(Context context) {
		super(context, 0, new ArrayList<MemoryKeyNote>());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView instanceof ViewGroup == false) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.key_note, null);
		}
		
		final Context context = getContext();
		
		MemoryKeyNote note = getItem(position);

		if (note != null && convertView instanceof ViewGroup) {
			TextView timeKeyView = (TextView) convertView.findViewById(
					R.id.key_note_time_key);
			if (timeKeyView != null) {
				final long time = note.getTimestamp();
				
				SimpleDateFormat sdf = new SimpleDateFormat(
						context.getString(R.string.time_print_simple_time_format_24));
				
				timeKeyView.setText(sdf.format(time));
			}
			
			TextView contentView = (TextView) convertView.findViewById(
					R.id.key_note_content);
			if (contentView != null) {
				contentView.setText(Html.fromHtml(
						replaceColorInContent(note.getContent())));
			}
			
			View lineView = convertView.findViewById(
					R.id.key_note_indicator);
			if (lineView != null) {
				final int count = getCount();
				
				int resid = R.drawable.keynote_line_mid;
				
				if (count <= 1) {
					resid = R.drawable.keynote_line_zero;
				} else {
					if (position == 0) {
						final boolean today = 
								CalendarUtils.isCurrentDay(note.getTimestamp());
						Logger.debug("keynotes timestamp = %d[%s]",
								note.getTimestamp(),
								CalendarUtils.timeToReadableString(note.getTimestamp()));
						resid = ((today && !note.isOverTheDayEnd()) 
								? R.drawable.keynote_line_end_now
										: R.drawable.keynote_line_end_past);
					} else if (position == getCount() - 1) {
						resid = R.drawable.keynote_line_start;
					}
				}
				
				lineView.setBackgroundResource(resid);
			}
		}

		return convertView;
	}

	private String replaceColorInContent(String origContent) {
		if (TextUtils.isEmpty(origContent)) {
			return origContent;
		}
		
		final Context context = getContext();
		final Resources res = context.getResources();
		if (res == null) {
			return origContent;
		}
		
		final int mainColor = res.getColor(R.color.gold_yellow);
		final int subColor = res.getColor(R.color.see_green);
		
		String replstr = origContent;
		
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_MAIN_COLOR,
					androidColorToHtml(mainColor));
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_MAIN_COLOR,
				String.format("#%2x%2x%2x", 
						Color.red(subColor),
						Color.green(subColor),
						Color.blue(subColor)));
		Logger.debug("orig = [%s], replstr = [%s]",
				origContent, replstr);
		
		return replaceStyleInContent(replstr);
	}
	
	private String replaceStyleInContent(String origContent) {
		if (TextUtils.isEmpty(origContent)) {
			return origContent;
		}
		
		final Context context = getContext();
		final Resources res = context.getResources();
		if (res == null) {
			return origContent;
		}
		
		String replstr = origContent;
		
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_MAIN_STYLE_START,
					KEY_NOTE_MAIN_STYLE_START);
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_MAIN_STYLE_END,
					KEY_NOTE_MAIN_STYLE_END);
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_SUB_STYLE_START,
					KEY_NOTE_SUB_STYLE_START);
		replstr = replstr.replaceAll(
				MemoryPieceKeyNotes.KEY_NOTE_USE_SUB_STYLE_END,
					KEY_NOTE_SUB_STYLE_END);
		Logger.debug("orig = [%s], replstr = [%s]",
				origContent, replstr);
		
		return replstr;
	}
	
	private String androidColorToHtml(int color) {
		return String.format("#%2x%2x%2x", 
				Color.red(color),
				Color.green(color),
				Color.blue(color));
	}
	
}
