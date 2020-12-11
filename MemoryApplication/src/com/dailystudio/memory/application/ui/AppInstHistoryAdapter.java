package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.AppInstHistory;
import com.dailystudio.memory.ui.AbsResObjectAdapter;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class AppInstHistoryAdapter extends AbsResObjectAdapter<AppInstHistory> {

	public AppInstHistoryAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_app_inst_history, null);
		}
		
		return convertView;
	}
	
	@Override
	protected void bindViewWithResource(View view, Context context,
			AppInstHistory object) {
		super.bindViewWithResource(view, context, object);
		
		if (object == null || context == null) {
			return;
		}
		
		ImageView actionView = (ImageView) view.findViewById(R.id.appinst_history_action_icon);
		if (actionView != null) {
			final String action = object.getPackageAction();
			
			int resId = -1;
			if (AppInstHistory.PACKAGE_ACTION_INSTALL.equals(action)) {
				resId = R.drawable.ic_app_inst;
			} else if (AppInstHistory.PACKAGE_ACTION_UNINSTALL.equals(action)) {
				resId = R.drawable.ic_app_uninst;
			} else if (AppInstHistory.PACKAGE_ACTION_UPDATE.equals(action)) {
				resId = R.drawable.ic_app_update;
			}
			
			actionView.setImageResource(resId);
		}
		
		TextView timeView = (TextView) view.findViewById(R.id.appinst_history_time);
		if (timeView != null) {
			final long time = object.getTime();
			
			timeView.setText(DateTimePrintUtils.printTimeStringWithoutTime(
					context, time));
		}
	}
	
}
