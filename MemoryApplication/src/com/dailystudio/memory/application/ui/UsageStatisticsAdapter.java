package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.ApplicationUsageStatistics;
import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.nativelib.application.AndroidApplication;

public class UsageStatisticsAdapter extends AppRelatedAdapter<UsageStatistics> {

	public UsageStatisticsAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_usage_statistics, null);
		}
		
		return convertView;
	}
	
	@Override
	protected void bindViewWithResource(View view, Context context,
			UsageStatistics object) {
		super.bindViewWithResource(view, context, object);
		
		if (object == null || context == null) {
			return;
		}
		
		TextView sumView = (TextView) view.findViewById(R.id.us_duration_sum);
		if (sumView != null) {
			final long durationSum = object.getDurationSum();
			
			CharSequence sumstr = null;
			if (durationSum >= 0) {
				sumstr = DateTimePrintUtils.printDurationString(
						context, durationSum);
			} 
			
			if (sumstr == null) {
				sumstr = context.getString(R.string.error_unknow);
			}
			
			sumView.setText(sumstr);
		}
	}

	@Override
	protected boolean isRelatedAppExisted(Context context,
			UsageStatistics us) {
		if (us instanceof ApplicationUsageStatistics == false) {
			return false;
		}
		
		final ApplicationUsageStatistics uas = 
				(ApplicationUsageStatistics) us;
		
		final AndroidApplication app = uas.getApplication();
		if (app == null) {
			return false;
		}
		
		return app.isInstalled(context);
	}
	
}
