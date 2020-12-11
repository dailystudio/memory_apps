package com.dailystudio.memory.where.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailystudio.memory.fragment.MemoryPeroidCursorListFragment;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.LoaderIds;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.loader.IdspotHistoryListLoader;
import com.dailystudio.memory.where.ui.IdspotHistoryAdapter;

public class IdspotHistoryListFragment extends MemoryPeroidCursorListFragment {

	private int mIdspotId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);
		
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		int idspotId = -1;
		
		if (args != null) {
			idspotId = args.getInt(Constants.EXTRA_IDSPOT_ID, -1);
		}
		
		return new IdspotHistoryListLoader(getActivity(),
				getPeroidStart(), getPeroidEnd(), idspotId);
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new IdspotHistoryAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_IDSPOT_HISTORY_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_IDSPOT_ID, mIdspotId);
		
		return args;
	}

	public void attachToIdspot(int idspotId) {
		mIdspotId = idspotId;
		
		restartLoader();
	}
	
}
