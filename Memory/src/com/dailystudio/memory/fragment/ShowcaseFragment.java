package com.dailystudio.memory.fragment;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.R;
import com.dailystudio.memory.showcase.Showcase;
import com.dailystudio.memory.ui.ShowcasePage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class ShowcaseFragment extends Fragment {

	private String mShowcasePackage;
	private Uri mShowcaseUri;
	private WebView mWebView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle bundle = getArguments();
		if (bundle != null) {
			final String pkgname = bundle.getString(
					Constants.EXTRA_SHOWCASE_PACKAGE, null);
			if (pkgname == null) {
				return;
			}
			
			mShowcasePackage = pkgname;
			
			final String uristr = bundle.getString(
					Constants.EXTRA_SHOWCASE_URI, null);
			if (uristr == null) {
				return;
			}
			
			mShowcaseUri = Uri.parse(uristr);
		}
		
		Logger.debug("mShowcasePackage = %s, mShowcaseUri = %s",
				mShowcasePackage, mShowcaseUri);
		startWatchShowcaseChanges();
		
		Showcase.prepareShowcase(getActivity(), mShowcasePackage);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_showcase, null);
		
		setupViews(view);
		
		refreshShowcase();
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopWatchShowcaseChanges();
	}
	
	private void setupViews(View fragmentView) {
		mWebView = (WebView) fragmentView.findViewById(R.id.showcase_webview);
	}
	
	private void refreshShowcase() {
		Logger.debug("mShowcaseUri = %s", mShowcaseUri);
		if (mShowcaseUri == null || mWebView == null) {
			return;
		}
		
		Logger.debug("[LOAD SHOWCASE] B: uri = %s", mShowcaseUri);
		mWebView.loadUrl(mShowcaseUri.toString());
		Logger.debug("[LOAD SHOWCASE] A: uri = %s", mShowcaseUri);
	}
	
	private void startWatchShowcaseChanges() {
		IntentFilter filter = new IntentFilter(
				Constants.ACTION_UPDATE_SHOWCASE);
		
		filter.addCategory(Constants.CATEGORY_MAIN);
		
		final Context context = getActivity();
		if (context == null) {
			return;
		}
		
		context.registerReceiver(mShowcaseUpdateReceiver, filter);
	}
	
	private void stopWatchShowcaseChanges() {
		final Context context = getActivity();
		if (context == null) {
			return;
		}
		
		context.unregisterReceiver(mShowcaseUpdateReceiver);
	}
	
	public static Fragment newInstance(Context context, ShowcasePage sp) {
		if (context == null || sp == null) {
			return null;
		}

		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_SHOWCASE_PACKAGE, sp.targetPackage);
		args.putString(Constants.EXTRA_SHOWCASE_URI, sp.targetUri.toString());

		Fragment frag = Fragment.instantiate(context,
				ShowcaseFragment.class.getName(), args);

		return frag;
	}

	private BroadcastReceiver mShowcaseUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.debug("intent = %s]",  intent);
			if (context == null || intent == null) {
				return;
			}
			
			final String pkg = intent.getStringExtra(
					Constants.EXTRA_SHOWCASE_PACKAGE);
			if (pkg == null) {
				return;
			}
			
			Logger.debug("pkg = %s[mShowcasePage = %s]", 
					pkg, mShowcasePackage);
			if (mShowcasePackage == null || mShowcasePackage.equals(pkg) == false) {
				return;
			}
			
			refreshShowcase();
		}
		
	};
	
}
