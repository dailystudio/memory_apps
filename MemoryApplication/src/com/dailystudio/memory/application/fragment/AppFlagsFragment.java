package com.dailystudio.memory.application.fragment;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class AppFlagsFragment extends Fragment {

	public static final int POSITION_INVALID = -1;
	public static final int POSITION_TOP = 0;
	public static final int POSITION_BOTTOM = 1;
	
	private final static long SHOW_FLAGS_DELAY = 500;
	
	private View mRoot;
	
	private Animation mInAnimation;
	private Animation mOutAnimation;
	
	private int mPosition = POSITION_INVALID;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_app_flags, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mRoot = fragmentView.findViewById(R.id.fragment_flags_root);
		
		final Context context = getActivity();
		
		mInAnimation = AnimationUtils.loadAnimation(context,
				R.anim.fade_in);
		mOutAnimation = AnimationUtils.loadAnimation(context,
				R.anim.fade_out);
	}
	
	public void moveFlagsTo(int position) {
		if (position == POSITION_INVALID) {
			return;
		}
		
		if (position == mPosition) {
			return;
		}
		
		if (mRoot == null) {
			return;
		}
		
		FrameLayout.LayoutParams flp = 
				new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
		
		if (position == POSITION_TOP) {
			flp.gravity = Gravity.TOP;
		} else if (position == POSITION_BOTTOM) {
			flp.gravity = Gravity.BOTTOM;
		}
		
		Logger.debug("position = %d", position);
		
		mRoot.setLayoutParams(flp);
		mRoot.requestLayout();
		
		mPosition = position;
	}
	
	public void showFlags() {
		mHandler.removeCallbacks(mShowFlagsRunnable);
		
		mHandler.postDelayed(mShowFlagsRunnable, SHOW_FLAGS_DELAY);
	}

	private void realShowFlags() {
		if (mRoot == null) {
			return;
		}
		
		if (mRoot.getVisibility() == View.VISIBLE) {
			return;
		}
		
		mRoot.setVisibility(View.VISIBLE);
		mRoot.startAnimation(mInAnimation);
	} 
	
	public void hideFlags() {
		mHandler.removeCallbacks(mShowFlagsRunnable);

		if (mRoot == null) {
			return;
		}
		
		if (mRoot.getVisibility() != View.VISIBLE) {
			return;
		}
		
		mRoot.startAnimation(mOutAnimation);
		mRoot.setVisibility(View.GONE);
	}

	private Runnable mShowFlagsRunnable = new Runnable() {
		
		@Override
		public void run() {
			realShowFlags();
			
		}
		
	};

	private Handler mHandler = new Handler();
	
}
