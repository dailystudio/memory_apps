package com.dailystudio.memory.application.fragment;

import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.LoaderIds;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.loader.AndroidApplicationsLoader;
import com.dailystudio.memory.application.ui.AndroidApplicationAdapter;
import com.dailystudio.memory.application.ui.ApplicationInstallTimeComparator;
import com.dailystudio.memory.application.ui.ApplicationNameComparator;
import com.dailystudio.memory.fragment.AbsResObjectListFragment;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.AppObservable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

public class AndroidApplicationListFragment extends AbsResObjectListFragment<AndroidApplication> {

	private int mFilterAppFlags = Constants.DEFAULT_APP_FILTER_FLAGS;
	private int mSortAppType = Constants.DEFAULT_APP_SORT_TYPE;
	
	private boolean mInManageMode = false;
		
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
		View view = inflater.inflate(R.layout.fragment_app_list, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
		final Resources res = getResources();
		if (res == null) {
			return;
		}
		
		int batchSize = 
			res.getInteger(R.integer.config_appsNotifyBatchSize);
		if (batchSize > 0) {
			setNotifyBatchSize(batchSize);
		}
	}

	@Override
	protected void bindAdapterView() {
		super.bindAdapterView();
		
		AdapterView<?> lv = getAdapterView();
		if (lv instanceof AbsListView == false) {
		    return;
		}
		
		((AbsListView)lv).setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mInManageMode) {
					return;
				}
				
				if (scrollState == SCROLL_STATE_IDLE) {
					showAppFlags();
				} else if (scrollState == SCROLL_STATE_FLING) {
					hideAppFlags();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mInManageMode) {
					return;
				}
				
				int position = AppFlagsFragment.POSITION_INVALID;
				if (firstVisibleItem <= visibleItemCount) {
					position = AppFlagsFragment.POSITION_BOTTOM;
				} else if (firstVisibleItem + visibleItemCount >= totalItemCount) {
					position = AppFlagsFragment.POSITION_TOP;
				}
				
/*				Logger.debug("f(%d), vc(%d) / tc(%d) -> position = %d",
						firstVisibleItem, visibleItemCount, totalItemCount,
						position);
*/				
				AppFlagsFragment fragment = getFlagsFragment();
				if (fragment != null) {
					fragment.moveFlagsTo(position);
				}
			}
			
		});

	}
	
	@Override
	public void onLoadFinished(Loader<List<AndroidApplication>> loader,
			List<AndroidApplication> data) {
		super.onLoadFinished(loader, data);
		if (mInManageMode) {
			hideAppFlags();
			
			return;
		}
	
		if (data == null || data.size() <= 0) {
			hideAppFlags();
		} else {
			showAppFlags();
		}
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
	
	private void showAppFlags() {
		AppFlagsFragment affrag = getFlagsFragment();
		if (affrag == null) {
			return;
		}
		
		affrag.showFlags();
	}
	
	private void hideAppFlags() {
		AppFlagsFragment affrag = getFlagsFragment();
		if (affrag == null) {
			return;
		}
		
		affrag.hideFlags();
	}

	private AppFlagsFragment getFlagsFragment() {
		Fragment fragment = 
				getFragmentManager().findFragmentById(R.id.fragment_list_flags);
		if (fragment instanceof AppFlagsFragment == false) {
			return null;
		}
		
		return (AppFlagsFragment)fragment;
	}

	public void filterApps(int filterAppFlag) {
		if (filterAppFlag == mFilterAppFlags) {
			Logger.warnning("SKIP filter apps with same flags: %d",
					filterAppFlag);
					
			return;
		}
		
		mFilterAppFlags = filterAppFlag;
		
		restartLoader();
	}
	
	public int getAppFilter() {
		return mFilterAppFlags;
	}

	public void sortApps(int sortType) {
		if (sortType == mSortAppType) {
			Logger.warnning("SKIP sort apps with same type: %d",
					sortType);
					
			return;
		}
		
		mSortAppType = sortType;
		
		sortAdapterIfPossible();
	}
	
	public int getSortType() {
		return mSortAppType;
	}

	public void startManageApps() {
		if (mInManageMode) {
			return;
		}
		
		mInManageMode = true;
		
		setEditEnabled(true);
		
		hideAppFlags();
	}
	
	public void stopManageApps() {
		if (!mInManageMode) {
			return;
		}
		
		mInManageMode = false;
		
		setEditEnabled(false);

		showAppFlags();
	}
	
	public boolean isManagingApps() {
		return mInManageMode;
	}
	
	private void setEditEnabled(boolean enabled) {
		final BaseAdapter adapter = getAdapter();
		if (adapter instanceof AndroidApplicationAdapter == false) {
			return;
		}
		
		AndroidApplicationAdapter appAdapter = 
			(AndroidApplicationAdapter)adapter;
		appAdapter.setEditEnabled(enabled);
	}
	
	@Override
	public Loader<List<AndroidApplication>> onCreateLoader(int loaderId, Bundle args) {
		Logger.debug("loaderId = %d, args = %s",
				loaderId,
				args);
		
		return new AndroidApplicationsLoader(getActivity(), mFilterAppFlags);
	}

	@Override
	protected BaseAdapter onCreateAdapter() {
		return new AndroidApplicationAdapter(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.LOADER_APPLICATION_LIST;
	}

	@Override
	protected Bundle createLoaderArguments() {
		Bundle bundle = new Bundle();
		
		bundle.putInt(Constants.EXTRA_APP_FILTER_FLAGS, mFilterAppFlags);
		
		return bundle;
	}
	
	@Override
	protected Comparator<AndroidApplication> getComparator() {
	    Comparator<AndroidApplication> comparator = sAppComparator;
		
		switch (mSortAppType) {
			case Constants.APP_SORT_BY_INSTALL_TIME:
				comparator = sAppInstallTimeComparator;
				break;
			
			case Constants.APP_SORT_BY_NAME:
				comparator = sAppNameComparator;
				break;
			
			case Constants.APP_SORT_BY_UPDATE_TIME:
			default:
				comparator = sAppComparator;
				break;
		}
		
		return comparator;
	}

	private Comparator<AndroidApplication> sAppComparator =
		new AndroidApplication.AndroidApplicationComparator();
	
	private Comparator<AndroidApplication> sAppInstallTimeComparator =
		new ApplicationInstallTimeComparator();
	
	private Comparator<AndroidApplication> sAppNameComparator =
		new ApplicationNameComparator();
	
	private Observer mAppObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			restartLoader();
		}
		
	};

}
