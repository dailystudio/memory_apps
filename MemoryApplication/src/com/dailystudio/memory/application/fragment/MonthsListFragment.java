package com.dailystudio.memory.application.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.databaseobject.Usage;
import com.dailystudio.memory.application.loader.UsageMonthsLoader;
import com.dailystudio.memory.dataobject.MonthObject;
import com.dailystudio.memory.fragment.AbsMonthsListFragment;

public class MonthsListFragment extends AbsMonthsListFragment<Usage> {

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_MONTHS;
	}

	@Override
	public Loader<List<MonthObject>> onCreateLoader(int arg0, Bundle arg1) {
		return new UsageMonthsLoader(getActivity(), 
				getPeroidStart(), getPeroidEnd());
	}


}
