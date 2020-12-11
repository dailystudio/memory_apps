package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.ApplicationUsageStatistics;
import com.dailystudio.memory.application.fragment.ApplicationUsageChartFragment;
import com.dailystudio.memory.ui.PaperClipLayout;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;

public class AppUsageInfoWindow extends PaperClipLayout {
	
    public AppUsageInfoWindow(Context context) {
        this(context, null);
    }

    public AppUsageInfoWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

	private void initMembers() {
		setContentView(R.layout.app_usage_info_window);
		
		setTitle(R.string.app_usage_info_window_title);
		setIcon(R.drawable.ic_app);
	}
	
	public void setAppUsage(ApplicationUsageStatistics appUs) {
		if (appUs == null) {
			reset();
		}
		
		final AndroidApplication application = appUs.getApplication();
		if (application == null) {
			reset();
			
			return;
		}
		
		final CharSequence label = appUs.getLabel();
		final String pkg = appUs.getPackageName();
		final boolean existed = application.isInstalled(mContext);

		setTitle((label == null ? 
				mContext.getString(R.string.error_unknow) : label));
		setTitleColorResource(existed ? R.color.black : R.color.gray);
		setIcon(appUs.getIcon());
		
		loadUsageInfo(pkg);
	}

	private void loadUsageInfo(String pkg) {
		if (pkg == null) {
			return;
		}
		
		final Context context = getContext();
		if (context instanceof FragmentActivity == false) {
			return;
		}
		
		FragmentManager fragmgr = 
			((FragmentActivity)context).getSupportFragmentManager();
		
		Fragment fragment = fragmgr.findFragmentById(
				R.id.fragment_app_usage_chart);
		if (fragment instanceof ApplicationUsageChartFragment == false) {
			return;
		}
		
		((ApplicationUsageChartFragment)fragment).loadUdageForPackage(pkg);
	}

	private void reset() {
		setTitle(R.string.app_info_window_title);
		setIcon(R.drawable.ic_app);
	}
 
}
