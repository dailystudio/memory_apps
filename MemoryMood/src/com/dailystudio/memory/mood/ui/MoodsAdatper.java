package com.dailystudio.memory.mood.ui;

import java.util.ArrayList;

import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MoodsAdatper extends ArrayAdapter<Mood> {

	private boolean mDisplayMoodLvl = false;
	private Animation mDisplayMoodLvlAnim = null;
	private Animation mHideMoodLvlAnim = null;
	
	public MoodsAdatper(Context context) {
		super(context, 0, new ArrayList<Mood>());
		
		mDisplayMoodLvlAnim =
			AnimationUtils.loadAnimation(context, R.anim.mood_lvl_display);
		mHideMoodLvlAnim =
			AnimationUtils.loadAnimation(context, R.anim.mood_lvl_hide);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView instanceof ViewGroup == false) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_mood, null);
		}
		
		Mood mood = getItem(position);
		if (mood != null && convertView != null) {
			ImageView iconView = (ImageView)convertView.findViewById(R.id.mood_icon);
			if (iconView != null) {
				iconView.setImageResource(mood.iconResId);
			}
			
			TextView lvlView = (TextView)convertView.findViewById(R.id.mood_level);
			if (lvlView != null) {
				final int oldVisibilty = lvlView.getVisibility();
				lvlView.setText(String.valueOf(mood.level));
				if (mDisplayMoodLvl) {
					if (oldVisibilty != View.VISIBLE) {
						lvlView.setVisibility(View.VISIBLE);
						lvlView.startAnimation(mDisplayMoodLvlAnim);
					}
				} else {
					if (oldVisibilty == View.VISIBLE) {
						lvlView.startAnimation(mHideMoodLvlAnim);
						lvlView.setVisibility(View.INVISIBLE);
					}
				}
			}
			
			TextView labelView = (TextView)convertView.findViewById(R.id.mood_label);
			if (labelView != null) {
				labelView.setText(mood.labelResId);
			}
		}
		
		return convertView;
	}
	
	public boolean isLevelDisplayed() {
		return mDisplayMoodLvl;
	}
	
	public void setLevelDisplayed(boolean displayed) {
		if (displayed == mDisplayMoodLvl) {
			return;
		}
		
		mDisplayMoodLvl = displayed;
		
		notifyDataSetChanged();
	}
	
}

