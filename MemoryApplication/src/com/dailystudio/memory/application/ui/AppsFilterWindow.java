package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.PaperClipLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppsFilterWindow extends PaperClipLayout {
	
	private CheckBox mCheckBoxUser;
	private CheckBox mCheckBoxSdCard;
	private CheckBox mCheckBoxSys;
	
    public AppsFilterWindow(Context context) {
        this(context, null);
    }

    public AppsFilterWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

	private void initMembers() {
		setContentView(R.layout.apps_filter_window);
		setTitle(R.string.app_filter_window_title);
		
		mCheckBoxUser = 
			(CheckBox) findViewById(R.id.check_user_apps);
		if (mCheckBoxUser != null) {
			mCheckBoxUser.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked == false) {
						if (mCheckBoxSdCard != null) {
							mCheckBoxSdCard.setChecked(false);
							mCheckBoxSdCard.setEnabled(false);
						}
					} else {
						if (mCheckBoxSdCard != null) {
							mCheckBoxSdCard.setEnabled(true);
						}
					}
				}
				
			});
		}
		
		mCheckBoxSdCard = 
			(CheckBox) findViewById(R.id.check_sd_apps);
		mCheckBoxSys = 
			(CheckBox) findViewById(R.id.check_sys_apps);
		
		setAppFilterFlags(Constants.DEFAULT_APP_FILTER_FLAGS);
	}
	
	public void setAppFilterFlags(int filterFlags) {
		if (mCheckBoxUser != null) {
			mCheckBoxUser.setChecked(
					((filterFlags & Constants.APP_FILTER_FLAG_USER) 
							== Constants.APP_FILTER_FLAG_USER));
		}
		
		if (mCheckBoxSdCard != null) {
			mCheckBoxSdCard.setChecked(
					((filterFlags & Constants.APP_FILTER_FLAG_SDCARD) 
							== Constants.APP_FILTER_FLAG_SDCARD));
		}
		
		if (mCheckBoxSys != null) {
			mCheckBoxSys.setChecked(
					((filterFlags & Constants.APP_FILTER_FLAG_SYSTEM) 
							== Constants.APP_FILTER_FLAG_SYSTEM));
		}
	}
	
	public int getAppFilterFlags() {
		int filterFlags = Constants.DEFAULT_APP_FILTER_FLAGS;
		
		if (mCheckBoxUser != null && mCheckBoxUser.isChecked()) {
			filterFlags = (filterFlags | Constants.APP_FILTER_FLAG_USER);
		} else {
			filterFlags = (filterFlags & ~Constants.APP_FILTER_FLAG_USER);
		}
		
		if (mCheckBoxSdCard != null && mCheckBoxSdCard.isChecked()) {
			filterFlags = (filterFlags | Constants.APP_FILTER_FLAG_SDCARD);
		} else {
			filterFlags = (filterFlags & ~Constants.APP_FILTER_FLAG_SDCARD);
		}
		
		if (mCheckBoxSys != null && mCheckBoxSys.isChecked()) {
			filterFlags = (filterFlags | Constants.APP_FILTER_FLAG_SYSTEM);
		} else {
			filterFlags = (filterFlags & ~Constants.APP_FILTER_FLAG_SYSTEM);
		}
		
		return filterFlags;
	}

}
