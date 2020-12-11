package com.dailystudio.memory.where.ui;

import java.io.File;

import com.dailystudio.development.Logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class OverlayWebView extends WebView {
	
	class AppWebChromeClient extends WebChromeClient { 

		@Override 
		public boolean onJsAlert(WebView view, String url, String message, 
				JsResult result) { 
			Toast.makeText(getContext(), message, 
					Toast.LENGTH_LONG).show(); 
			
			return true; 
		} 
		
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
	        Logger.debug("[line: %d, lvl: %s]: %s", 
	        		consoleMessage.lineNumber(),
	        		consoleMessage.messageLevel(),
	        		consoleMessage.message());
	        
			return true;
		}

	} 

	public OverlayWebView(Context context) {
        this(context, null);
    }

    public OverlayWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

    public OverlayWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
	private void initMembers() {
    	setBackgroundColor(Color.TRANSPARENT);
    	
        WebSettings webSettings = getSettings();
        if (webSettings != null) {
        	webSettings.setBuiltInZoomControls(false);
        	webSettings.setJavaScriptEnabled(true);
        	webSettings.setAppCacheEnabled(false);
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
        
        setWebChromeClient(new AppWebChromeClient());
        
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
 	}

	@Override
    public boolean performLongClick() {
    	return false;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
    
}
