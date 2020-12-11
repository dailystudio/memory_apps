package com.dailystudio.memory.ui;

import java.io.File;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.javascript.JSIntentLauncherPlugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class CardWebView extends WebView {
	
	private class CardWebChromeClient extends WebChromeClient { 

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
	        Logger.debug("[line: %d, lvl: %s]: %s", 
	        		consoleMessage.lineNumber(),
	        		consoleMessage.messageLevel(),
	        		consoleMessage.message());
			return true;
		}

	} 

	public CardWebView(Context context) {
        this(context, null);
    }

    public CardWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
        setupViews();
	}
    
    private void setupViews() {
		WebSettings settings = getSettings();
		if (settings != null) {
			settings.setBuiltInZoomControls(false);
			settings.setJavaScriptEnabled(true);
			settings.setAppCacheEnabled(false);
			settings.setSavePassword(true);
			settings.setSaveFormData(true);
        	settings.setDomStorageEnabled(true);
        	settings.setLightTouchEnabled(true);
        	settings.setLoadWithOverviewMode(true);
        	
        	File file = getContext().getDatabasePath("dummy.db");
    		Logger.debug("file = %s", file);
        	
    		settings.setDatabaseEnabled(true);
    		if (file != null) {
    			settings.setDatabasePath(file.getParent());
        	}
    		
    		settings.setGeolocationEnabled(true);
    		if (file != null) {
    			settings.setGeolocationDatabasePath(file.getParent());
        	}
		}
		
		setWebChromeClient(new CardWebChromeClient());

		addJavascriptInterface(new JSIntentLauncherPlugin(), "jsintent");
    }
    
	@Override
    public boolean performLongClick() {
    	return false;
    }

}
