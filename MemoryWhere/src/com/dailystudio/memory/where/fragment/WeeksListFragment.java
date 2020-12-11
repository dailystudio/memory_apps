package com.dailystudio.memory.where.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.dailystudio.memory.dataobject.WeekObject;
import com.dailystudio.memory.fragment.AbsWeeksListFragment;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.loader.IdspotHistoryWeeksLoader;

public class WeeksListFragment extends AbsWeeksListFragment<IdspotHistory> {

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_WEEKS;
	}

	@Override
	public Loader<List<WeekObject>> onCreateLoader(int arg0, Bundle arg1) {
		return new IdspotHistoryWeeksLoader(getActivity(),
				getPeroidStart(), getPeroidEnd());
	}

}
