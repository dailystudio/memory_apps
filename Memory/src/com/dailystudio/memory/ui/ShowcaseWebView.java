package com.dailystudio.memory.ui;

import java.io.File;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShowcaseWebView extends PluginsObserverWebView {
	
	public ShowcaseWebView(Context context) {
        this(context, null);
    }

    public ShowcaseWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowcaseWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
        WebSettings webSettings = getSettings();
        if (webSettings != null) {
        	webSettings.setBuiltInZoomControls(false);
        	webSettings.setAppCacheEnabled(false);
    		webSettings.setSavePassword(true);
        	webSettings.setSaveFormData(true);
        	webSettings.setDomStorageEnabled(true);
        	webSettings.setLightTouchEnabled(true);
        	webSettings.setLoadWithOverviewMode(true);
        	
        	File file = getContext().getDatabasePath("dummy.db");
    		Logger.debug("file = %s", file);
        	
    		webSettings.setDatabaseEnabled(true);
    		if (file != null) {
          		webSettings.setDatabasePath(file.getParent());
        	}
    		
    		webSettings.setGeolocationEnabled(true);
    		if (file != null) {
          		webSettings.setGeolocationDatabasePath(file.getParent());
        	}
    	}
        
        setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		Logger.debug("url = %s", url);
        		super.onPageFinished(view, url);
        	}

        	@Override
        	public void onReceivedError(WebView view, int errorCode,
        			String description, String failingUrl) {
        		Logger.debug("errorCode = %d, description = %s, failingUrl = %s", 
        				errorCode, description, failingUrl);
        		switch (errorCode) {
        		case WebViewClient.ERROR_UNSUPPORTED_SCHEME:
        			case WebViewClient.ERROR_FILE_NOT_FOUND:
        				loadUrl("file:///android_res/raw/default_showcase_404_page.html");
        				return;
        		}
        	
        		super.onReceivedError(view, errorCode, description, failingUrl);
        	}
        	
        });
        
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        
        Logger.debug("layre type = %d", getLayerType());
	}

    @Override
    protected void onPluginsChanged() {
    	super.onPluginsChanged();
    	
    	reload();
    }
	
}
