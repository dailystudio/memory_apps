package com.dailystudio.memory.application.ui;

import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.AndroidApplication.AndroidApplicationComparator;

public class ApplicationNameComparator extends AndroidApplicationComparator {
	
	@Override
	public int compare(AndroidApplication app1, AndroidApplication app2) {
		int ret = 0;
		
		final CharSequence label1 = app1.getLabel();
		final CharSequence label2 = app2.getLabel();
		
		if (label1 == null) {
			return -1;
		} else if (label2 == null) {
			return 1;
		}
		
		ret = label1.toString().compareTo(label2.toString());
		if (ret != 0) {
			return ret;
		}
		
		return super.compare(app1, app2);
	}

}
