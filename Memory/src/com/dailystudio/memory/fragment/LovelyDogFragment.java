package com.dailystudio.memory.fragment;

import com.dailystudio.app.ui.AnimatedImageView;
import com.dailystudio.memory.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LovelyDogFragment extends Fragment {

	private AnimatedImageView mDogAnimView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lovely_dog, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mDogAnimView = (AnimatedImageView) fragmentView.findViewById(
				R.id.lovely_dog_anim);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mDogAnimView != null) {
			mDogAnimView.playAnimation();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mDogAnimView != null) {
			mDogAnimView.stopAnimation();
		}
	}

}
