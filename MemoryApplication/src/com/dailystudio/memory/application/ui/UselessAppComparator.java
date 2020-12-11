package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.databaseobject.UselessApp;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject.ResourceObjectComparator;

public class UselessAppComparator extends ResourceObjectComparator<UselessApp> {
	
	@Override
	public int compare(UselessApp uapp1, UselessApp uapp2) {
        if (uapp1 == null) {
            return -1;
        } else if (uapp2 == null) {
            return 1;
        }
    
		final AndroidApplication app1 = uapp1.getApplication();
		final AndroidApplication app2 = uapp2.getApplication();

		if (app1 == null) {
			return -1;
		} else if (app2 == null) {
			return 1;
		}
	
		final boolean install1 = app1.isInstalled(uapp1.getContext());
		final boolean install2 = app2.isInstalled(uapp2.getContext());
		
		if (install1 == false && install2 != false) {
			return 1;
		} else if (install2 == false && install1 != false) {
			return -1;
		}
		
		return 0;
	}

}
