package com.dailystudio.memory.mood.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.dailystudio.memory.mood.LoaderIds;
import com.dailystudio.memory.mood.MemoryMood;
import com.dailystudio.memory.mood.loader.MoodWeeksLoader;
import com.dailystudio.memory.dataobject.WeekObject;
import com.dailystudio.memory.fragment.AbsWeeksListFragment;

public class WeeksListFragment extends AbsWeeksListFragment<MemoryMood> {

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_WEEKS_LOADER;
	}

	@Override
	public Loader<List<WeekObject>> onCreateLoader(int arg0, Bundle arg1) {
		return new MoodWeeksLoader(getActivity(),
				getPeroidStart(),
				getPeroidEnd());
	}
	
}
