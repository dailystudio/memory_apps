package com.dailystudio.memory.fragment;

import com.dailystudio.memory.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PrivacyPolicyFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_privacy_policy, null);
		
		setupViews(view);
		
		return view;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
	}

}
