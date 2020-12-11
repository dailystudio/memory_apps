package com.dailystudio.memory.where.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public class IdspotHistoryAdapter extends SimpleDatabaseObjectCursorAdapter<IdspotHistory> {

	public IdspotHistoryAdapter(Context context) {
		super(context, R.layout.idspot_history_item, IdspotHistory.class);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		final IdspotHistory history = dumpItem(c);
		if (history == null) {
			return;
		}
		
		final HotspotIdentity identity = history.getIdentity();
		if (identity == null) {
			return;
		}
		
		TextView latview = (TextView) view.findViewById(R.id.geo_lat);
		if (latview != null) {
			latview.setText(String.format("%.9f", history.getLatitude()));
		}

		TextView lonview = (TextView) view.findViewById(R.id.geo_lon);
		if (lonview != null) {
			lonview.setText(String.format("%.9f", history.getLongitude()));
		}

	    final HotspotIdentityInfo hiInfo = 
	            HotspotIdentifier.getIdentityInfo(identity);
	    
	    ImageView iconView = (ImageView) view.findViewById(R.id.loc_icon);
	    if (iconView != null) {
	        iconView.setImageResource(hiInfo.iconResId);
	    }
	    
		String timestr = null;
		TextView startView = (TextView) view.findViewById(R.id.idspot_history_start);
		if (startView != null) {
			timestr = DateTimePrintUtils.printTimeString(
					context, history.getTime());
			startView.setText(timestr);
		}

		TextView endView = (TextView) view.findViewById(R.id.idspot_history_end);
		if (endView != null) {
			long endTime = 0l;
			final long duration = history.getDuration();
			final boolean lastOne = 
					IdspotHistoryDatabaseModal.isLastHistory(context, history);
			if (lastOne && duration <= 0) {
				endTime = System.currentTimeMillis();
			} else {
				endTime = history.getTime() + duration;
			}
			
			timestr = DateTimePrintUtils.printTimeString(
					context, endTime);
			endView.setText(timestr);
		}

	}
	
}
