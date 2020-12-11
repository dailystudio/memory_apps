package com.dailystudio.memory.fragment;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.memory.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PromoteAppsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_promote_apps, null);
		
		setupViews(view);
		
		return view;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}

		final WebView webView = (WebView) fragmentView.findViewById(R.id.promote_webpage);
		if (webView != null) {
			webView.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, final String url) {
					if (Uri.parse(url).getScheme().equals("market")) {

						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));

						ActivityLauncher.launchActivity(getActivity(), intent,
								new ActivityLauncher.OnExceptionHandler() {
									@Override
									public void onException(Intent intent, Exception e) {
										Uri uri = Uri.parse(url);

										webView.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
									}
								});
						return true;
					}

					return false;
				}

			});
		}
	}
}
