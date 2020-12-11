package com.dailystudio.memory.application.ui;

import com.dailystudio.memory.application.databaseobject.UsageStatistics;
import com.dailystudio.nativelib.application.IResourceObject.ResourceObjectComparator;

public class UsageStatisticsComparator extends ResourceObjectComparator<UsageStatistics> {
	
	@Override
	public int compare(UsageStatistics us1, UsageStatistics us2) {
		long lret = 0;
		
		final long durationSum1 = us1.getDurationSum();
		final long durationSum2 = us2.getDurationSum();
		lret = durationSum1 - durationSum2;
		if (lret != 0) {
			return (lret > 0 ? -1: 1);
		}
		
		return super.compare(us1, us2);
	}

}
