package com.dailystudio.memory.fragment;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.AboutLoader;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.nativelib.application.AndroidApplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends AbsLoaderFragment<AndroidApplication> {

    private TextView mVerNameView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
        if (fragmentView == null) {
            return;
        }

        mVerNameView = (TextView) fragmentView.findViewById(R.id.about_app_ver_name);
	}

    @Override
    protected int getLoaderId() {
        return LoaderIds.MEMORY_ABOUT_LOADER;
    }

    @Override
    protected Bundle createLoaderArguments() {
        return new Bundle();
    }

    @Override
    public Loader<AndroidApplication> onCreateLoader(int id, Bundle args) {
        return new AboutLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<AndroidApplication> loader, AndroidApplication data) {
        bindVerName(data);
    }

    @Override
    public void onLoaderReset(Loader<AndroidApplication> loader) {
        bindVerName(null);
    }

    private void bindVerName(AndroidApplication thisApp) {
        if (mVerNameView == null) {
            return;
        }

        if (thisApp == null) {
            mVerNameView.setText(R.string.verName);
        } else {
            mVerNameView.setText(thisApp.getVersionName());
        }
    }

}
