package com.dailystudio.memory.where.ui;


import com.dailystudio.development.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class EnvironmentOverlay extends TextView {
	
	private final static int COLOR_TRANSPARENT = 0x00FFFFFF;

	private final static int DAY_NIGHT_COLOR_BASE = 0x00000000;
	private final static int DAY_NIGHT_COLOR_ALPHA = 0xD0;
	
	private final static int HAZE_COLOR_BASE = 0x00FFFFFF;
	private final static int HAZE_COLOR_ALPHA = 0x80;
	private final static int HAZE_LEVEL_MAX = 5;

	protected Context mContext;
	
	private int mDayNightColor = COLOR_TRANSPARENT;
	private int mHazeColor = COLOR_TRANSPARENT;
	private int mRainColor = COLOR_TRANSPARENT;
	
	private int mHour;
	private int mHazeLvl;
	
	public EnvironmentOverlay(Context context) {
        this(context, null);
    }

    public EnvironmentOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnvironmentOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
    }

	private void initMembers() {
	}
	
	public void setHour(int hour) {
		mHour = hour;
		
		syncDayNightColor();
	}
	
	public void syncDayNightColor() {
		int hour = mHour;

		int alpha = DAY_NIGHT_COLOR_ALPHA;
		int sunrisze = 8;
		int sunset = 18;
		
		if (hour >= 0 && hour < sunrisze) {
			alpha = (int)(alpha * (1 - (hour / (float)sunrisze)));
		} else if (hour > sunset) {
			alpha = (int)(alpha * (1 - (24 - hour) / (24 - sunset)));
		} else {
			alpha = 0;
		}
		
		mDayNightColor = (alpha << 24) | DAY_NIGHT_COLOR_BASE;
		
		invalidate();
	}
	
	public void setHazeLevel(int lvl) {
		mHazeLvl = lvl;
		
		syncHazeColor();
	}
	
	public void syncHazeColor() {
		int lvl = mHazeLvl;
		
		if (lvl < 0) {
			lvl = 0;
		}

		int alpha = HAZE_COLOR_ALPHA;
		
		alpha = alpha * lvl / HAZE_LEVEL_MAX;
		
		mHazeColor = (alpha << 24) | HAZE_COLOR_BASE;
		
		Logger.debug("lvl = %d, mHazeColor = 0x%08x",
				lvl, mHazeColor);
		
		invalidate();
	}
	
	public void setRainLevel(int lvl) {
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
//		Logger.debug("mDayNightColor = 0x%08x", mDayNightColor);
		canvas.drawColor(mDayNightColor);
		canvas.drawColor(mHazeColor);
		canvas.drawColor(mRainColor);
	}
	
}
