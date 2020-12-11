package com.dailystudio.memory.lifestyle.ui;

import com.dailystudio.memory.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class LifeActivitiesLayout extends LinearLayout {
	
	protected Context mContext;
	
	public LifeActivitiesLayout(Context context) {
		this(context, null);
	}
	
	public LifeActivitiesLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LifeActivitiesLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		
		initMembers();
	}

	private void initMembers() {
		LayoutInflater.from(mContext).inflate(R.layout.card_layout, this);
		
		setupViews();
	}

	private void setupViews() {
	}

}
