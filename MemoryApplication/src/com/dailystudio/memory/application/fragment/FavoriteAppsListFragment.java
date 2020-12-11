package com.dailystudio.memory.application.fragment;

import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.application.loader.FavoriteAppsLoader;
import com.dailystudio.memory.application.ui.FavoriteAppsListAdapter;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class FavoriteAppsListFragment extends AbsResObjectListFragment<FavoriteApp> {

	private boolean mIncludeSysApps = false;
	private int mFavoriteClass = Constants.FAVORITE_CLASS_WEEK;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			mFavoriteClass = bundle.getInt(Constants.EXTRA_FAVORITE_CLASS, 
					Constants.FAVORITE_CLASS_WEEK);
			mIncludeSysApps = bundle.getBoolean(Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS, 
					false);
		}
		
		Logger.debug("mFavoriteClass = %d, mIncludeSysApps = %s",
				mFavoriteClass, mIncludeSysApps);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		NativeObservable appObservable = 
			ObservableManager.getObservable(AppObservable.class);
		if (appObservable != null) {
			appObservable.addObserver(mAppObserver);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_noanim, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		NativeObservable appObservable = 
			ObservableManager.getObservable(AppObservable.class);
		if (appObservable != null) {
			appObservable.deleteObserver(mAppObserver);
		}
	}

	@Override
	public Loader<List<FavoriteApp>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		int favoriteClass = Constants.FAVORITE_CLASS_WEEK;
		boolean includeSysApp = false;
		if (args != null) {
			favoriteClass = args.getInt(Constants.EXTRA_FAVORITE_CLASS,
					Constants.FAVORITE_CLASS_WEEK);
			includeSysApp = args.getBoolean(
					Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS,
					false);
		}
		
		return new FavoriteAppsLoader(getActivity(),
				includeSysApp, favoriteClass);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new FavoriteAppsListAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		Logger.debug("mFavoriteClass = %d",
				mFavoriteClass);
		return (LoaderIds.LOADER_FAVORITE_APPS_LIST_BASE | mFavoriteClass);
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_FAVORITE_CLASS, mFavoriteClass);
		args.putBoolean(Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS, mIncludeSysApps);
		
		return args;
	}
		
	@Override
	protected Comparator<FavoriteApp> getComparator() {
	    return null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		super.onItemClick(l, v, position, id);
		
		final BaseAdapter adapter = getAdapter();
		if (adapter == null) {
			return;
		}
		
		Object data = adapter.getItem(position);
		if (data instanceof FavoriteApp == false) {
			return;
		}
		
		FavoriteApp fApp = (FavoriteApp)data;
		final Context context = getActivity();
		
		final AndroidApplication app = 
				new AndroidApplication(fApp.getPackageName());
		Logger.debug("app = %s [launchIntent: %s]", 
				app, app.getLaunchIntent(context));
		app.launch(context);
	}

	public static Fragment newInstance(Context context, 
			int favoriteClass, boolean includeSysApps) {
		if (context == null) {
			return null;
		}

		Bundle args = new Bundle();
		
		args.putInt(Constants.EXTRA_FAVORITE_CLASS, favoriteClass);
		args.putBoolean(Constants.EXTRA_FAVORITE_INCLUE_SYS_APPS, includeSysApps);

		Fragment frag = Fragment.instantiate(context,
				FavoriteAppsListFragment.class.getName(), args);

		return frag;
	}


	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};

}
