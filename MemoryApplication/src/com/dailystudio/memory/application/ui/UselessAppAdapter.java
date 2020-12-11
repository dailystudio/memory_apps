package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UselessApp;
import com.dailystudio.nativelib.application.AndroidApplication;

public class UselessAppAdapter extends AppRelatedAdapter<UselessApp> {

	public UselessAppAdapter(Context context) {
		super(context);
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_useless_app, null);
		}
		
		return convertView;
	}
	
	@Override
	protected void bindViewWithResource(View view, Context context,
			UselessApp object) {
		super.bindViewWithResource(view, context, object);
		
		if (object == null || context == null) {
			return;
		}
		
		ImageView notUsedView = (ImageView) view.findViewById(R.id.useless_app_not_used_icon);
		if (notUsedView != null) {
			notUsedView.setImageResource(object.isNotUsedRecently() ?
					R.drawable.ic_app_not_used : R.drawable.ic_app_used);
		}

		ImageView notUpdatedView = (ImageView) view.findViewById(R.id.useless_app_not_updated_icon);
		if (notUpdatedView != null) {
			notUpdatedView.setImageResource(object.isNotUpdatedRecently() ?
					R.drawable.ic_app_not_updated : R.drawable.ic_app_updated);
		}
		
		final boolean existed = isRelatedAppExisted(getContext(), object);
		
		View iconsView = view.findViewById(R.id.useless_app_icons);
		if (iconsView != null) {
			iconsView.setVisibility(existed ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	protected boolean isRelatedAppExisted(Context context, UselessApp object) {
		final AndroidApplication app = object.getApplication();
		if (app == null) {
			return false;
		}
		
		return app.isInstalled(context);
	}
	
}
