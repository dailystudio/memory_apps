package com.dailystudio.memory.application.ui;

import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.AndroidApplication.AndroidApplicationComparator;

public class ApplicationInstallTimeComparator extends AndroidApplicationComparator {
	
	@Override
	public int compare(AndroidApplication app1, AndroidApplication app2) {
		long lret = 0;
		
		final long installTime1 = app1.getFirstInstallTime();
		final long installTime2 = app2.getFirstInstallTime();
		lret = installTime1 - installTime2;
		if (lret != 0) {
			return (lret > 0 ? -1: 1);
		}
		
		return super.compare(app1, app2);
	}

}
