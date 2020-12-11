package com.dailystudio.memory.application.ui;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.UselessApp;
import com.dailystudio.memory.ui.PaperClipLayout;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.widget.TextView;

public class UselessAppInfoWindow extends PaperClipLayout {
	
	private final int DAYS_IN_MONTH = 30;
	
	private TextView mInfoPackage;
	private TextView mInfoLastUpdate;
	private TextView mInfoLastUsage;

	private View mInfoBtnRun;
	private View mInfoBtnUninstall;
	
	private UselessApp mApplication;
	
    public UselessAppInfoWindow(Context context) {
        this(context, null);
    }

    public UselessAppInfoWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

	private void initMembers() {
		setContentView(R.layout.useless_app_info_window);
		
		setTitle(R.string.app_info_window_title);
		setIcon(R.drawable.ic_app);
		
		mInfoPackage = (TextView) findViewById(R.id.info_package);
		mInfoLastUsage = (TextView) findViewById(R.id.info_recent_used);
		mInfoLastUpdate = (TextView) findViewById(R.id.info_recent_updated);
		
		mInfoBtnRun = findViewById(R.id.info_btn_run);
		if (mInfoBtnRun != null) {
			mInfoBtnRun.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mApplication != null) {
						final AndroidApplication application =
								mApplication.getApplication();
						if (application == null) {
							return;
						}

						application.launch(mContext);
					}
				}
				
			});
		}
		
		mInfoBtnUninstall = findViewById(R.id.info_btn_uninstall);
		if (mInfoBtnUninstall != null) {
			mInfoBtnUninstall.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mApplication != null) {
						final AndroidApplication application =
								mApplication.getApplication();
						if (application == null) {
							return;
						}

						application.uninstall(mContext);
					}
				}
				
			});
		}
	}
	
	public void setUselessApp(UselessApp usapp) {
		if (usapp == null) {
			reset();
		}
		
		mApplication = usapp;
		
		final AndroidApplication application = mApplication.getApplication();
		if (application == null) {
			reset();
			
			return;
		}
		
		
		final CharSequence label = usapp.getLabel();
		final CharSequence pkg = usapp.getPackageName();
		final long lastUpdate = usapp.getRecentUpdatedTime();
		final long lastUsage = usapp.getRecentUsedTime();
		final boolean existed = application.isInstalled(mContext);
		
		
		setTitle((label == null ? 
				mContext.getString(R.string.error_unknow) : label));
		setTitleColorResource(existed ? R.color.black : R.color.gray);
		setIcon(usapp.getIcon());
		
		if (mInfoBtnRun != null) {
			mInfoBtnRun.setVisibility((application.isLaunchable(mContext) ?
					View.VISIBLE : View.GONE));
		}
		
		if (mInfoBtnUninstall != null) {
			mInfoBtnUninstall.setVisibility(
					((!application.isSystem() && application.isInstalled(mContext)) ?
					View.VISIBLE : View.GONE));
		}
		
		if (mInfoPackage != null) {
			mInfoPackage.setText((pkg == null ? 
					mContext.getString(R.string.error_unknow) : pkg));
		}
		
		final long now = System.currentTimeMillis();
		
		String str = null;
		int day = 0;
		int month = 0;
		if (mInfoLastUpdate != null) {
			if (lastUpdate < 0) {
				str = mContext.getString(R.string.useless_app_info_never_updated);
			} else {
				day = (int)((now - lastUpdate) / CalendarUtils.DAY_IN_MILLIS);
				if (day <= DAYS_IN_MONTH) {
					str = getResources().getQuantityString(
							R.plurals.useless_app_info_recent_val_day_templ, 
							day,
							day);
				} else {
					month = (int) Math.ceil((float)day / DAYS_IN_MONTH);
					str = getResources().getQuantityString(
							R.plurals.useless_app_info_recent_val_month_templ, 
							month,
							month);
				}
			}
			
			mInfoLastUpdate.setText(str);
		}
		
		if (mInfoLastUsage != null) {
			if (lastUsage < 0) {
				str = mContext.getString(R.string.useless_app_info_never_used);
			} else {
				day = (int)((now - lastUsage) / CalendarUtils.DAY_IN_MILLIS);
				if (day <= DAYS_IN_MONTH) {
					str = getResources().getQuantityString(
							R.plurals.useless_app_info_recent_val_day_templ, 
							day,
							day);
				} else {
					month = (int) Math.ceil((float)day / DAYS_IN_MONTH);
					str = getResources().getQuantityString(
							R.plurals.useless_app_info_recent_val_month_templ, 
							month,
							month);
				}
			}
			
			mInfoLastUsage.setText(str);
		}
	}

	private void reset() {
		mApplication = null;
		
		setTitle(R.string.app_info_window_title);
		setIcon(R.drawable.ic_app);
		
		if (mInfoPackage != null) {
			mInfoPackage.setText(null);
		}
		
		if (mInfoLastUpdate != null) {
			mInfoLastUpdate.setText(null);
		}
		
		if (mInfoLastUsage != null) {
			mInfoLastUsage.setText(null);
		}
	}
 
}
