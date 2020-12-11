package com.dailystudio.memory.ui;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.card.Cards;
import com.dailystudio.memory.querypiece.MemoryPieceCard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CardLayout extends FrameLayout {
	
	protected Context mContext;
	
	private TextView mTitleView;
	private CardWebView mContentWebView;
	
	public CardLayout(Context context) {
		this(context, null);
	}
	
	public CardLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		
		initMembers();
	}

	private void initMembers() {
		LayoutInflater.from(mContext).inflate(R.layout.card_layout, this);
		
		setupViews();
	}

	private void setupViews() {
		mTitleView = (TextView) findViewById(R.id.card_title);
		
		mContentWebView = (CardWebView) findViewById(R.id.card_webview);
		if (mContentWebView != null) {
			mContentWebView.setWebViewClient(mWebViewClient);
		}
	}
	
	public void bindCard(MemoryPieceCard card) {
		if (card == null) {
			return;
		}
		
		if (mTitleView != null) {
			mTitleView.setText(card.getCardTitle());
		}
		
		if (mContentWebView != null) {
			ComponentName comp = card.getPluginComponent();
			if (comp != null) {
				Uri uri = Uri.parse("content://" 
						+ comp.getPackageName() + ".cards"
						+ "/"
						+ card.getCardUri());
				Logger.debug("load card url: %s", uri);
				
				mContentWebView.loadUrl(uri.toString());
			}
		}
	}

	
	private WebViewClient mWebViewClient = new WebViewClient() {
		
		public void onReceivedError(WebView view, int errorCode, 
				String description, String failingUrl) {
			Logger.debug("errorCode = %d[%s], url = %s",
					errorCode, description, failingUrl);
			if (errorCode == ERROR_FILE_NOT_FOUND
					|| errorCode == ERROR_UNSUPPORTED_SCHEME) {
				Uri uri = Uri.parse("content://" 
						+ getContext().getPackageName() + ".cards"
						+ "/"
						+ Cards.CARD_DEFAULT_FILE);
				Logger.debug("load default card url: %s", uri);
				mContentWebView.loadUrl(uri.toString());
			}
		};
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Logger.debug("url = %s", url);
			Intent intent = new Intent(Intent.ACTION_VIEW, 
					Uri.parse(url));
	        
			ActivityLauncher.launchActivity(getContext(), intent);
	        
	        return true;
		}
		

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		};
		
		public void onPageFinished(WebView view, String url) {
			if (view == null) {
				super.onPageFinished(view, url);
				
				return;
			}
			
			final String title = view.getTitle();
			Logger.debug("card title: %s", title);
			if (!TextUtils.isEmpty(title)) {
				if (mTitleView != null) {
					mTitleView.setText(title);
				}
			}
			
			super.onPageFinished(view, url);
		};
		
	};

}
