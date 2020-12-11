package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.PaperClipLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class AppsSortWindow extends PaperClipLayout {
	
	private RadioButton mRadioSortByName;
	private RadioButton mRadioSortByInstallTime;
	private RadioButton mRadioSortByUpdateTime;
	
    public AppsSortWindow(Context context) {
        this(context, null);
    }

    public AppsSortWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

	private void initMembers() {
		setContentView(R.layout.apps_sort_window);
		setTitle(R.string.app_sort_window_title);
		
		mRadioSortByName = (RadioButton) findViewById(R.id.radio_sort_by_name);
		mRadioSortByInstallTime = 
			(RadioButton) findViewById(R.id.radio_sort_by_install_time);
		mRadioSortByUpdateTime = 
			(RadioButton) findViewById(R.id.radio_sort_by_update_time);
		
		setSortType(Constants.DEFAULT_APP_SORT_TYPE);
	}
	
	public void setSortType(int sortType) {
		switch (sortType) {
			case Constants.APP_SORT_BY_NAME:
				if (mRadioSortByName != null) {
					mRadioSortByName.setChecked(true);
				}
				break;
				
			case Constants.APP_SORT_BY_UPDATE_TIME:
				if (mRadioSortByUpdateTime != null) {
					mRadioSortByUpdateTime.setChecked(true);
				}
				break;
				
			case Constants.APP_SORT_BY_INSTALL_TIME:
			default:
				if (mRadioSortByInstallTime != null) {
					mRadioSortByInstallTime.setChecked(true);
				}
				break;
		}
	}

	public int getSortType() {
		int sortType = Constants.DEFAULT_APP_SORT_TYPE;
		
		if (mRadioSortByName != null && mRadioSortByName.isChecked()) {
			sortType = Constants.APP_SORT_BY_NAME;
		} else if (mRadioSortByUpdateTime != null && mRadioSortByUpdateTime.isChecked()) {
			sortType = Constants.APP_SORT_BY_UPDATE_TIME;
		} else if (mRadioSortByInstallTime != null && mRadioSortByInstallTime.isChecked()) {
			sortType = Constants.APP_SORT_BY_INSTALL_TIME;
		}
		
		return sortType;
	}
	
}
