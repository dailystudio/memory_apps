package com.dailystudio.memory.application.ui;

import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.nativelib.application.AndroidActivity;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class UsageAdapter extends AppRelatedAdapter<Usage> {

	public UsageAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_usage, null);
		}
		
		return convertView;
	}
	
	@Override
	protected void bindViewWithResource(View view, Context context,
			Usage object) {
		super.bindViewWithResource(view, context, object);
		
		if (object == null || context == null) {
			return;
		}
		
		TextView timeView = (TextView) view.findViewById(R.id.us_time);
		if (timeView != null) {
			final long time = object.getTime();
			
			CharSequence timestr = null;
			if (time >= 0) {
				timestr = DateTimePrintUtils.printTimeString(
						context, time);
			} 
			
			if (timestr == null) {
				timestr = context.getString(R.string.error_unknow);
			}
			
			timeView.setText(timestr);
		}
		
		TextView durationView = (TextView) view.findViewById(R.id.us_duration);
		if (durationView != null) {
			final long duration = object.getDuration();
			
			CharSequence durationstr = null;
			if (duration >= 0) {
				durationstr = DateTimePrintUtils.printDurationString(
						context, duration);
			} 
			
			if (durationstr == null) {
				durationstr = context.getString(R.string.error_unknow);
			}
			
			durationView.setText(durationstr);
		}
	}

	@Override
	protected boolean isRelatedAppExisted(Context context,
			Usage usage) {
		IResourceObject ro = usage.getResObject();

		if (ro instanceof AndroidActivity == false) {
			return false;
		}
		
		AndroidActivity activity = (AndroidActivity)ro;
		
		final ComponentName comp = activity.getComponentName();
		if (comp == null) {
			return false;
		}
		
		return AndroidApplication.isInstalled(context, comp.getPackageName());
	}
	
}
