package com.dailystudio.memory.ui;

import java.text.SimpleDateFormat;
import java.util.List;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.fragment.KeyNotesListFragment;
import com.dailystudio.memory.lifestyle.TimeSpan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class KeynotesPageAdapter extends FragmentStatePagerAdapter {
	
	private Context mContext;
	
	private List<TimeSpan> mKeynotesTimeSpans;
	
	public KeynotesPageAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mKeynotesTimeSpans == null) {
			return 0;
		}
		
		return mKeynotesTimeSpans.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (mKeynotesTimeSpans == null) {
			return super.getPageTitle(position);
		}
		
		if (position < 0 || position >= mKeynotesTimeSpans.size()) {
			return super.getPageTitle(position);
		}
		
		final TimeSpan timespan = mKeynotesTimeSpans.get(position);
		
		String title = null;
		if (isToday(timespan.start)) {
			title = mContext.getString(R.string.date_label_today);
		} else if (isYesterday(timespan.start)) {
			title = mContext.getString(R.string.date_label_yesterday);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(
					mContext.getString(R.string.time_print_date_format_without_year));
			
			title = sdf.format(timespan.start);
		}
				
/*		Logger.debug("timespan = %s, title = %s",
				timespan, title);
*/
		return title;
	}

	@Override
	public Fragment getItem(int position) {
		if (mKeynotesTimeSpans == null) {
			return null;
		}
		
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		final TimeSpan ts = mKeynotesTimeSpans.get(position);
//		Logger.debug("keynotes ts = %s", ts);
		if (ts == null) {
			return null;
		}
		
		Fragment fragment = 
			KeyNotesListFragment.newInstance(mContext, ts);
		
		return fragment;
	}
	
	public void setTimespans(List<TimeSpan> timespans) {
		Logger.debug("keynotes spans = %s", timespans);
		mKeynotesTimeSpans = timespans;
		
		notifyDataSetChanged();
	}
	
	private boolean isToday(long time) {
		return CalendarUtils.isCurrentDay(time);
	}

	private boolean isYesterday(long time) {
		final long now = System.currentTimeMillis();
		
		final long startOfYestory = 
			CalendarUtils.getStartOfDay(now - CalendarUtils.DAY_IN_MILLIS);
		
		return (CalendarUtils.getStartOfDay(time) == startOfYestory);
	}


}
