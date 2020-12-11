package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.PaperClipLayout;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AppInfoWindow extends PaperClipLayout {
	
	private TextView mInfoPackage;
	private TextView mInfoPath;
	private TextView mInfoVersion;
	private TextView mInfoLastUpdate;
	private TextView mInfoFirstInstall;

	private View mInfoBtnManage;
	private View mInfoBtnRun;
	private View mInfoBtnUninstall;
	
	private AndroidApplication mApplication;
	
    public AppInfoWindow(Context context) {
        this(context, null);
    }

    public AppInfoWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

	private void initMembers() {
		setContentView(R.layout.app_info_window);
		
		setTitle(R.string.app_info_window_title);
		setIcon(R.drawable.ic_app);
		
		mInfoPackage = (TextView) findViewById(R.id.info_package);
		mInfoPath = (TextView) findViewById(R.id.info_path);
		mInfoVersion = (TextView) findViewById(R.id.info_version);
		mInfoLastUpdate = (TextView) findViewById(R.id.info_last_update);
		mInfoFirstInstall = (TextView) findViewById(R.id.info_first_install);
		
		mInfoBtnManage = findViewById(R.id.info_btn_manage);
		if (mInfoBtnManage != null) {
			mInfoBtnManage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mApplication != null) {
						mApplication.manage(mContext);
					}
				}
				
			});
		}
		
		mInfoBtnRun = findViewById(R.id.info_btn_run);
		if (mInfoBtnRun != null) {
			mInfoBtnRun.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mApplication != null) {
						mApplication.launch(mContext);
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
						mApplication.uninstall(getContext());
					}
				}
				
			});
		}
	}
	
	public void setApplication(AndroidApplication app) {
		if (app == null) {
			reset();
		}
		
		mApplication = app;
		
		final CharSequence label = app.getLabel();
		final CharSequence pkg = app.getPackageName();
		final CharSequence path = app.getPackagePath();
		final CharSequence ver = app.getVersionName();
		final long lastUpdate = app.getLastUpdateTime();
		final long firstInstall = app.getFirstInstallTime();
		
		setTitle((label == null ? 
				mContext.getString(R.string.error_unknow) : label));
		setIcon(app.getIcon());
		
		if (mInfoBtnRun != null) {
			mInfoBtnRun.setVisibility((mApplication.isLaunchable(mContext) ?
					View.VISIBLE : View.GONE));
		}
		
		if (mInfoBtnUninstall != null) {
			mInfoBtnUninstall.setVisibility((!mApplication.isSystem() ?
					View.VISIBLE : View.GONE));
		}
		
		if (mInfoPackage != null) {
			mInfoPackage.setText((pkg == null ? 
					mContext.getString(R.string.error_unknow) : pkg));
		}
		
		if (mInfoPath != null) {
			mInfoPath.setText((path == null ? 
					mContext.getString(R.string.error_unknow) : path));
		}
		
		if (mInfoVersion != null) {
			mInfoVersion.setText((ver == null ? 
					mContext.getString(R.string.error_unknow) : ver));
		}
		
		if (mInfoLastUpdate != null) {
			mInfoLastUpdate.setText((lastUpdate < 0 ? 
					mContext.getString(R.string.error_unknow) 
					: DateTimePrintUtils.printTimeString(mContext, lastUpdate)));
		}
		
		if (mInfoFirstInstall != null) {
			mInfoFirstInstall.setText((firstInstall < 0 ? 
					mContext.getString(R.string.error_unknow) 
					: DateTimePrintUtils.printTimeString(mContext, firstInstall)));
		}
	}

	private void reset() {
		mApplication = null;
		
		setTitle(R.string.app_info_window_title);
		setIcon(R.drawable.ic_app);
		
		if (mInfoPackage != null) {
			mInfoPackage.setText(null);
		}
		
		if (mInfoVersion != null) {
			mInfoVersion.setText(null);
		}
		
		if (mInfoLastUpdate != null) {
			mInfoLastUpdate.setText(null);
		}
		
		if (mInfoFirstInstall != null) {
			mInfoFirstInstall.setText(null);
		}
	}
 
}
