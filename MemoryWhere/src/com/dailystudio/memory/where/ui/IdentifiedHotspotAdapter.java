package com.dailystudio.memory.where.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspotDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdentifiedLocationObject;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdspotHistorySummary;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public class IdentifiedHotspotAdapter extends ArrayAdapter<IdentifiedLocationObject> {
	
	protected long mPeroidStart = -1;
	protected long mPeroidEnd = -1;
	
	public IdentifiedHotspotAdapter(Context context, long start, long end) {
		super(context, 0, new ArrayList<IdentifiedLocationObject>());
		
		mPeroidStart = start;
		mPeroidEnd = end;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = getContext();
		
		if (convertView == null)  {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.identified_hotspot_item, null);
		}
		
		final IdentifiedLocationObject locObject = getItem(position);
		Logger.debug("locObject = %s", locObject);
		final HotspotIdentity identity = locObject.getIdentity();
		Logger.debug("identity = %s", identity);
		
		if (locObject != null
		        && identity != null
		        && convertView != null) {
			IdentifiedHotspot idspot = null;
			if (locObject instanceof IdspotHistorySummary) {
				idspot = IdentifiedHotspotDatabaseModal.getIdentifiedHotspot(
						 context, ((IdspotHistorySummary)locObject).getIdentifiedHotspotId());
				if (idspot == null) {
					idspot = IdentifiedHotspotDatabaseModal.getOldIdentifiedHotspot(
							context, ((IdspotHistorySummary)locObject).getIdentifiedHotspotId());
				}
			} else {
				idspot = (IdentifiedHotspot) locObject;
			}
			
		    final HotspotIdentityInfo hiInfo = 
		            HotspotIdentifier.getIdentityInfo(identity);
			Logger.debug("hiInfo = %s", hiInfo);
		    
		    ImageView iconView = (ImageView) convertView.findViewById(R.id.loc_icon);
		    if (iconView != null && hiInfo != null) {
		        iconView.setImageResource(hiInfo.iconResId);
		    }

		    TextView latview = (TextView) convertView.findViewById(R.id.geo_lat);
			if (latview != null) {
				latview.setText(String.format("%.9f", locObject.getLatitude()));
			}
	
			TextView lonview = (TextView) convertView.findViewById(R.id.geo_lon);
			if (lonview != null) {
				lonview.setText(String.format("%.9f", locObject.getLongitude()));
			}
	
			TextView typeView = (TextView) convertView.findViewById(R.id.idspot_type);
			if (typeView != null && hiInfo != null) {
				typeView.setText(hiInfo.labelResId);
			}
	
			Logger.debug("idspot = %s", idspot);
            TextView similarityView = (TextView) convertView.findViewById(
                    R.id.idspot_similarity);
            if (similarityView != null) {
                final String simLabel = context.getString(
                        R.string.label_similarity);
                double similarity = 0.f;
                
                if (idspot != null) {
                	similarity = idspot.getSimilarity();
                }
                
                similarityView.setText(String.format("(%s: %2.1f%%)",
                        simLabel, similarity * 100));
            }
    
			TextView durationView = (TextView) convertView.findViewById(R.id.hotspot_duration);
			if (durationView != null) {
				long duration = 0l;
				if (locObject instanceof IdspotHistorySummary) {
					IdspotHistorySummary summary = (IdspotHistorySummary)locObject;
					
					duration = summary.getSumDuration();
					if (IdspotHistoryDatabaseModal.isSummaryOfLastHistory(
							context, summary)) {
						final IdspotHistory lastHistory = 
								IdspotHistoryDatabaseModal.getLastHistory(context);
						
						if (lastHistory != null) {
							final long lastDuration = lastHistory.getDuration();
							if (lastDuration == 0) {
								final long estimateDuration = 
										System.currentTimeMillis() - lastHistory.getTime();
								
								if (estimateDuration > 0) {
									duration += estimateDuration;
								}
							}
						}
					}
					
				}
				
				if (hiInfo != null) {
					final long now = System.currentTimeMillis();
					
					int dayPass = 1;
					if (mPeroidStart == -1 || CalendarUtils.isCurrentWeek(mPeroidStart)) {
						dayPass = Math.round(
							(now - CalendarUtils.getStartOfWeek(now)) 
								/ (float)CalendarUtils.DAY_IN_MILLIS);
					} else {
						dayPass = (hiInfo.weekdaysFilter == null 
								? 7 : hiInfo.weekdaysFilter.length);
					}
					
					if (dayPass <= 0) {
						dayPass = 1;
					}
					
					Logger.debug("[identity = %s]: duration(%s) / dayPass(%d) = avg(%s)",
							identity, 
							CalendarUtils.durationToReadableString(duration), 
							dayPass, 
							CalendarUtils.durationToReadableString(duration / dayPass));
					
					duration = duration / dayPass ;
				}	
				
				if (duration <= 0) {
					durationView.setVisibility(View.GONE);
				} else {
					durationView.setVisibility(View.VISIBLE);
					
					String tmpl = context.getString(R.string.idspot_stat_every_avg_tmpl);
					String durText = DateTimePrintUtils.printDurationString(
							context, duration);
					
					if (tmpl != null) {
						durationView.setText(String.format(tmpl, durText));
					} else {
						durationView.setText(durText);
						
					}
				}
			}
		}
		
		return convertView;
	}
	
}
