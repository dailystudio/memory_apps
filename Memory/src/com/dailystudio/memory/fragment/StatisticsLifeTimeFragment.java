package com.dailystudio.memory.fragment;

import com.dailystudio.app.ui.DurationCountView;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.StatisticsLifetimeLoader;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatisticsLifeTimeFragment extends AbsStatisticsFragment<TimeCapsule> {
	
	public static final String STATS_TOKEN = "stats-lifetime";
	
	private DurationCountView mLifetimeValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_stats_lifetime, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mLifetimeValue = (DurationCountView) fragmentView.findViewById(
				R.id.stats_lifetime_value);
	}
	
	@Override
	public Loader<TimeCapsule> onCreateLoader(int loaderId, Bundle arg1) {
		notifyStatisticBegin();
		
		return new StatisticsLifetimeLoader(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_STATISTICS_LIFETIME_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<TimeCapsule> arg0, TimeCapsule data) {
		Logger.debug("data = %s", data);
		
		if (data instanceof MemoryBoot == false) {
			return;
		}
		
		final long now = System.currentTimeMillis();
		final long start = ((MemoryBoot)data).getTime();
		
		final long lifetime = (now - start);

		if (mLifetimeValue != null) {
			mLifetimeValue.setOnCountDurationListener(mOnCountDurationListener);
			
			mLifetimeValue.setDuration(lifetime);
		}
	}

	@Override
	public void onLoaderReset(Loader<TimeCapsule> arg0) {
		notifyStatisticEnd();
	}

	@Override
	String getStatisticToken() {
		return STATS_TOKEN;
	}

	private DurationCountView.OnCountDurationListener mOnCountDurationListener = new DurationCountView.OnCountDurationListener() {
		
		@Override
		public void onCountDurationStart(DurationCountView dv, long duration) {
		}
		
		@Override
		public void onCountDurationFinished(DurationCountView dv, long duration) {
			notifyStatisticEnd();
		}
		
	};
	

}
