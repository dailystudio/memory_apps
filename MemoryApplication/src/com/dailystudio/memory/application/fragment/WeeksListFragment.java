package com.dailystudio.memory.application.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.loader.UsageWeeksLoader;
import com.dailystudio.memory.dataobject.WeekObject;
import com.dailystudio.memory.fragment.AbsWeeksListFragment;

public class WeeksListFragment extends AbsWeeksListFragment<Usage> {

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_WEEKS;
	}

	@Override
	public Loader<List<WeekObject>> onCreateLoader(int arg0, Bundle arg1) {
		return new UsageWeeksLoader(getActivity(), 
				getPeroidStart(), getPeroidEnd());
	}

}
