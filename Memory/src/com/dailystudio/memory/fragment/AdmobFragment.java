package com.dailystudio.memory.fragment;

import com.dailystudio.app.widget.DeferredHandler;
import com.dailystudio.memory.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdmobFragment extends Fragment {

	private AdView mAdmobView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admob, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}

		mAdmobView = (AdView) fragmentView.findViewById(R.id.admob);
		if (mAdmobView != null) {
			mHandler.postIdle(new Runnable() {
				
				@Override
				public void run() {
					requestAd();
				}

			});
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		destoryAd();
	}
	
	private void requestAd() {
		if (mAdmobView != null) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdmobView.loadAd(adRequest);
		}
	}
	
	private void destoryAd() {
		if (mAdmobView != null) {
			mAdmobView.destroy();
		}
	}

	private DeferredHandler mHandler = new DeferredHandler();
	
}
