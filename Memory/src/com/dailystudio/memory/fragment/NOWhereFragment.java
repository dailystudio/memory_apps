package com.dailystudio.memory.fragment;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.QueryNOWhereLoader;
import com.dailystudio.memory.querypieces.NOWhere;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NOWhereFragment extends AbsLoaderFragment<String> {
	
	private ImageView mLocIcon;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_query_nowhere, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mLocIcon = (ImageView)fragmentView.findViewById(R.id.loc_icon);
	}
	
	@Override
	public Loader<String> onCreateLoader(int loaderId, Bundle arg1) {
		return new QueryNOWhereLoader(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_QUERY_NOW_WHERE_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}

	@Override
	public void onLoadFinished(Loader<String> arg0, String data) {
		if (mLocIcon != null) {
			if (TextUtils.isEmpty(data)) {
				mLocIcon.setImageDrawable(null);
				mLocIcon.setVisibility(View.INVISIBLE);
			} else {
				boolean matched = false;
				if (NOWhere.HOTSPOT_HOME.equals(data)) {
					mLocIcon.setImageResource(R.drawable.nowhere_query_home);
					matched = true;
				} else if (NOWhere.HOTSPOT_WORKPLACE.equals(data)) {
					mLocIcon.setImageResource(R.drawable.nowhere_query_workplace);
					matched = true;
				}
				
				mLocIcon.setVisibility(matched ? View.VISIBLE : View.INVISIBLE);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		if (mLocIcon != null) {
			mLocIcon.setImageDrawable(null);
			mLocIcon.setVisibility(View.INVISIBLE);
		}
	}

	public void updateNOWhere() {
		restartLoader();
	}

}
