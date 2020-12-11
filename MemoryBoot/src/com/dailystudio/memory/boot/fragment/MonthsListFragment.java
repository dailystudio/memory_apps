package com.dailystudio.memory.boot.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dailystudio.app.fragment.AbsArrayAdapterFragment;
import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.BootMonthsLoader;
import com.dailystudio.memory.boot.ui.MonthsAdatper;
import com.dailystudio.memory.dataobject.MonthObject;

public class MonthsListFragment extends AbsArrayAdapterFragment<MonthObject> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<List<MonthObject>> onCreateLoader(int arg0, Bundle arg1) {
		return new BootMonthsLoader(getActivity());
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new MonthsAdatper(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_BOOT_MONTHS_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

}
