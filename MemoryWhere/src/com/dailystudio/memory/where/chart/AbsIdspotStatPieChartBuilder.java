package com.dailystudio.memory.where.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.view.View;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.TimeSpanUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.chart.ChartBuilder;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public abstract class AbsIdspotStatPieChartBuilder extends ChartBuilder<IdspotHistory> {
	
	public class SharedArguments {

		public long minTime = 0;
		public long maxTime = 0;
		
		public Map<HotspotIdentity, Long> mSumDurations = 
				new HashMap<HotspotIdentity, Long>();
		
		private void updateMinAndMaxTime(long start, long end) {
			if (minTime == 0) {
				minTime = start;
			} else {
				if (start < minTime) {
					minTime = start;
				}
			}

			if (maxTime == 0) {
				maxTime = end;
			} else {
				if (end > maxTime) {
					maxTime = end;
				}
			}
		}
		
		private void calculateDuration(IdspotHistory history) {
			if (history == null) {
				return;
			}
			
			final HotspotIdentity identity = 
					history.getIdentity();
			if (identity == null) {
				return;
			}
			
			long duration = history.getDuration();
			long endTime = 0;
			
			if (duration == 0 
					&& IdspotHistoryDatabaseModal.isLastHistory(
							getContext(), history)) {
				endTime = System.currentTimeMillis(); 
			} else {
				endTime = history.getTime() + history.getDuration();
			}
			
			duration = TimeSpanUtils.calculateOverlapDuration(
						history.getTime(), 
						endTime,
						getPeroidStart(),
						getPeroidEnd());
			
			long oldDuration = 0l;
			
			if (mSumDurations.containsKey(identity)) {
				oldDuration = mSumDurations.get(identity);
			}
			
/*			Logger.debug("history: %s, NEW DURATION[%s]: old[%s] + duration[%s]", 
					history,
					CalendarUtils.durationToReadableString(oldDuration + duration),
					CalendarUtils.durationToReadableString(oldDuration),
					CalendarUtils.durationToReadableString(duration));
*/
			mSumDurations.put(identity, (oldDuration + duration));
			
			updateMinAndMaxTime(history.getTime(), endTime);
		}
		
		@Override
		public String toString() {
			return String.format("[%s - %s], sumDurations = %s", 
					CalendarUtils.timeToReadableString(minTime),
					CalendarUtils.timeToReadableString(maxTime),
					mSumDurations);
		}

	}
	
	private long mPeroidStart;
	private long mPeroidEnd;

	public AbsIdspotStatPieChartBuilder(Context context) {
		this(context, -1, -1);
	}

	public AbsIdspotStatPieChartBuilder(Context context, long start, long end) {
		super(context);
		
		setPeroid(start, end);
	}

	public void setPeroid(long start, long end) {
		mPeroidStart = start;
		mPeroidEnd = end;
		
		if (mPeroidStart < 0) {
			mPeroidStart = System.currentTimeMillis();
		}
		
		if (mPeroidEnd < mPeroidStart) {
			mPeroidEnd = mPeroidStart;
		}
	}
	
	public long getPeroidStart() {
		return mPeroidStart;
	}
	
	public long getPeroidEnd() {
		return mPeroidEnd;
	}

	@Override
	public Object createShareArguments() {
		return new SharedArguments();
	}
	
	@Override
	public Object buildDataset(List<IdspotHistory> objects,
			Object sharedArguments) {
		if (objects == null) {
			return null;
		}
		
		SharedArguments args = null;
		
		if (sharedArguments instanceof SharedArguments) {
			args = (SharedArguments)sharedArguments;
		}
		
		if (args == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		IdspotHistory history = null;
		for (DatabaseObject object: objects) {
			if (object instanceof IdspotHistory == false) {
				continue;
			}
			
			history = ((IdspotHistory)object);

			args.calculateDuration(history);
		}
		
		if (args.mSumDurations == null ||
				args.mSumDurations.size() <= 0) {
			return null;
		}
		
		String seriesTitle = 
				context.getString(R.string.chart_label_idspot);
		
		CategorySeries series = new CategorySeries(seriesTitle);
		
		HotspotIdentityInfo hInfo = null;
		long hour = 0;
		long sumDuration = 0;
		
		List<HotspotIdentity> sortedIdentities = 
				sortIdentities(args.mSumDurations.keySet());
	    if (sortedIdentities == null) {
	    	return null;
	    }
		
		for (HotspotIdentity identity: sortedIdentities) {
			hInfo = HotspotIdentifier.getIdentityInfo(identity);
			if (hInfo == null) {
				args.mSumDurations.remove(identity);
				continue;
			}
			
			seriesTitle = context.getString(hInfo.labelResId);
			
			hour = (args.mSumDurations.get(identity) 
					/ CalendarUtils.HOUR_IN_MILLIS);
					
			Logger.debug("identity: %s, duration[%d hour], loader = 0x%08x, dataset = %s", 
					identity,
					hour,
					this.hashCode(), 
					series);
			series.add(seriesTitle, hour);
			
			args.mSumDurations.put(identity, hour);
			
			sumDuration += hour;
		}

		Logger.debug("sumDuration[%d]", sumDuration);
		
		final long now = System.currentTimeMillis();
		final long start = Math.max(args.minTime, getPeroidStart());
		long end = Math.min(args.maxTime, getPeroidEnd());
		if (end > now) {
			end = now;
		} 
		
		final long hours = (int)Math.ceil((end - start) / CalendarUtils.HOUR_IN_MILLIS);
		long remainHours = Math.round(hours - sumDuration);
		if (remainHours <= 0) {
			remainHours = 0;
		}
		Logger.debug("[%s - %s], total hours[%d], remain %d hours", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end),
				hours, remainHours);
		
		seriesTitle = context.getString(R.string.hotspot_unknow);
		series.add(seriesTitle, remainHours);
		args.mSumDurations.put(
				HotspotIdentity.HOTSPOT_UNKNOWN, remainHours);
		
		Logger.debug("loader = 0x%08x, sharedArguments = %s, dataset = %s",
				this.hashCode(), sharedArguments, series);

		return series;
	}


	@Override
	public View getChart(Object dataset, Object renderer) {
		if (dataset instanceof CategorySeries == false) {
			return null;
		}
		
		if (renderer instanceof DefaultRenderer == false) {
			return null;
		}
		
		return ChartFactory.getPieChartView(
				getContext(), 
	    		(CategorySeries)dataset, 
	    		(DefaultRenderer)renderer);
	}

	public static List<HotspotIdentity> sortIdentities(Set<HotspotIdentity> identities) {
		if (identities == null) {
			return null;
		}
		
		List<HotspotIdentity> list = new ArrayList<HotspotIdentity>();
		for (HotspotIdentity identity: identities) {
			list.add(identity);
		}
		
		Collections.sort(list);
		
		return list;
	}

}
