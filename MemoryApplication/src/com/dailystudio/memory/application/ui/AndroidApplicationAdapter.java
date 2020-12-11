package com.dailystudio.memory.application.ui;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.fragment.UninstallSystemAppDialogFragment;
import com.dailystudio.memory.ui.AbsResObjectAdapter;
import com.dailystudio.memory.ui.AlertDialogFragment;
import com.dailystudio.nativelib.application.AndroidApplication;

public class AndroidApplicationAdapter extends AbsResObjectAdapter<AndroidApplication> {

	private boolean mInEditFlag;
	
	public AndroidApplicationAdapter(Context context) {
		super(context);
		
		mInEditFlag = false;
	}

	@Override
	protected View createViewIfRequired(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.layout_application, null);
		}
		
		return convertView;
	}

	@Override
	protected void bindViewWithResource(View view, Context context,
			AndroidApplication app) {
		super.bindViewWithResource(view, context, app);
		
		if (view == null || app == null) {
			return;
		}
		
		view.setClickable(mInEditFlag);
		
		ViewGroup flagsView = (ViewGroup) view.findViewById(R.id.app_flags);
		if (flagsView != null) {
			if (mInEditFlag) {
				flagsView.setVisibility(View.INVISIBLE);
			} else {
				flagsView.setVisibility(View.VISIBLE);
				bindFlags(flagsView, app);
			}
		}
			
		ImageView uninstView = 
			(ImageView) view.findViewById(R.id.app_uninstall);
		if (uninstView != null) {
			if (mInEditFlag) {
				uninstView.setVisibility(View.VISIBLE);
				
				bindUninstallView(uninstView, app);
				attachApplication(uninstView, app);
			} else {
				uninstView.setVisibility(View.INVISIBLE);
				detachApplication(uninstView);
			}
		}
		
	}
	
	public void setEditEnabled(boolean enabled) {
		if (mInEditFlag == enabled) {
			return;
		}
		
		mInEditFlag = enabled;
		
		notifyDataSetChanged();
	}
	
	private void bindFlags(View flagsView, AndroidApplication app) {
		if (flagsView == null || app == null) {
			return;
		}
		
		ImageView flag1View = (ImageView) flagsView.findViewById(R.id.app_flag_1);
		if (flag1View != null) {
			if (app.isSystem()) {
				flag1View.setImageResource(R.drawable.ic_app_flag_sys);
			} else {
				flag1View.setImageResource(R.drawable.ic_app_flag_user);
			}
		}
		
		ImageView flag2View = (ImageView) flagsView.findViewById(R.id.app_flag_2);
		if (flag2View != null) {
			if (!app.isSystem() && app.isOnSdCard()) {
				flag2View.setImageResource(R.drawable.ic_app_flag_sd);
			} else {
				flag2View.setImageResource(R.drawable.ic_app_flag_unset);
			}
		}
		
		ImageView f3View = (ImageView) flagsView.findViewById(R.id.app_flag_3);
		if (f3View != null) {
			if (app.isDebuggable()) {
				f3View.setImageResource(R.drawable.ic_app_flag_debuggable);
			} else {
				f3View.setImageResource(R.drawable.ic_app_flag_unset);
			}
		}
		
		ImageView f4View = (ImageView) flagsView.findViewById(R.id.app_flag_4);
		if (f4View != null) {
			if (app.isPersistent()) {
				f4View.setImageResource(R.drawable.ic_app_flag_persist);
			} else {
				f4View.setImageResource(R.drawable.ic_app_flag_unset);
			}
		}
	}
	
	private void bindUninstallView(ImageView uninstView, AndroidApplication app) {
		if (uninstView == null || app == null) {
			return;
		}
		
		if (app.isSystem()) {
			uninstView.setImageResource(R.drawable.ic_app_uninstall_system);
		} else {
			uninstView.setImageResource(R.drawable.ic_app_uninstall);
		}
	}
	
	private void attachApplication(ImageView uninstView, AndroidApplication app) {
		if (uninstView == null || app == null) {
			return;
		}
		
		WeakReference<AndroidApplication> appRef = 
			new WeakReference<AndroidApplication>(app);
		
		uninstView.setTag(appRef);
		uninstView.setOnClickListener(mOnUninstallViewClickListener);
	}
	
	private void detachApplication(ImageView uninstView) {
		if (uninstView == null) {
			return;
		}
		
		uninstView.setTag(null);
		uninstView.setOnClickListener(null);
	}
	
	private OnClickListener mOnUninstallViewClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == null) {
				return;
			}
			
			Object tag = v.getTag();
			if (tag instanceof WeakReference == false) {
				return;
			}

			final Context context = getContext();
			if (context == null) {
				return;
			}
			
			@SuppressWarnings("unchecked")
			WeakReference<AndroidApplication> appRef =
				(WeakReference<AndroidApplication>)tag;
			
			AndroidApplication app = appRef.get();
			if (app == null) {
				return;
			}
			
			final String appPkg = app.getPackageName();
			final CharSequence appLabel = app.getLabel();
			final boolean isSystem = app.isSystem();
			
			Logger.debug("Uninstall app[%s(%s), system: %s]", 
					appLabel,
					appPkg,
					isSystem);
			
			if (appPkg == null || appLabel == null) {
				return;
			}
			
			if (isSystem) {
				alertSystemAppUninstall(context, app);
			} else {
				app.uninstall(context);
			}
		}
		
		private void alertSystemAppUninstall(Context context, 
				AndroidApplication app) {
			if (context == null || app == null) {
				return;
			}
			
			if (context instanceof FragmentActivity == false) {
				return;
			}
			
			final String appPkg = app.getPackageName();
			final CharSequence appLabel = app.getLabel();
			final boolean isSystem = app.isSystem();
			
			Logger.debug("Uninstall app[%s(%s), system: %s]", 
					appLabel,
					appPkg,
					isSystem);
			
			if (appPkg == null || appLabel == null) {
				return;
			}
			
			AlertDialogFragment uninstAppConfirmFragment = 
				new UninstallSystemAppDialogFragment();
			
			Bundle args = new Bundle();
			
			args.putString(Constants.EXTRA_APP_LABEL, appLabel.toString());
			
			uninstAppConfirmFragment.setArguments(args);
			
			uninstAppConfirmFragment.show(
					((FragmentActivity)context).getSupportFragmentManager(), 
					"uninst-app-confirm");
		}
		
	};
	
}
