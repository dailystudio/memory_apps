package com.dailystudio.memory.fragment;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.datetime.CalendarUtils;
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
import android.widget.TextView;

public class SliderLifeTimeFragment extends AbsLoaderFragment<TimeCapsule> {
	
	private TextView mLifetimeValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_slider_lifetime, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mLifetimeValue = (TextView) fragmentView.findViewById(
				R.id.stats_lifetime_value);
	}
	
	@Override
	public Loader<TimeCapsule> onCreateLoader(int loaderId, Bundle arg1) {
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

		setDay(lifetime / CalendarUtils.DAY_IN_MILLIS);
	}

	@Override
	public void onLoaderReset(Loader<TimeCapsule> arg0) {
		setDay(0);
	}
	
	private void setDay(long day) {
		if (mLifetimeValue != null) {
			final String dayTempl = getString(
					R.string.slider_life_time_day);
			
			mLifetimeValue.setText(String.format(dayTempl, day));
		}
	}

}
