package com.dailystudio.memory.fragment;

import com.dailystudio.app.fragment.AbsLoaderFragment;

public abstract class AbsStatisticsFragment<T> extends AbsLoaderFragment<T> {

	public static interface StatisticCallbacks {
		
		public void onStatisticBegin(String token);
		public void onStatisticEnd(String token);
		
	}
	
	private StatisticCallbacks mStatisticCallbacks;
	
	protected void notifyStatisticBegin() {
		if (mStatisticCallbacks != null) {
			mStatisticCallbacks.onStatisticBegin(getStatisticToken());
		}
	}
	
	protected void notifyStatisticEnd() {
		if (mStatisticCallbacks != null) {
			mStatisticCallbacks.onStatisticEnd(getStatisticToken());
		}
	}
	
	public void setStatisticCallbacks(StatisticCallbacks callbacks) {
		mStatisticCallbacks = callbacks;
	}
	
	abstract String getStatisticToken();
	
}
