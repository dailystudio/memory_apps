package com.dailystudio.memory.ui;

import java.util.Observable;
import java.util.Observer;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.javascript.MemoryJSPluginManager;
import com.dailystudio.memory.plugin.PluginObserverable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class PluginsObserverWebView extends WebView {
	
	class DebugWebChromeClient extends WebChromeClient { 

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
	        Logger.debug("[line: %d, lvl: %s]: %s", 
	        		consoleMessage.lineNumber(),
	        		consoleMessage.messageLevel(),
	        		consoleMessage.message());
			return true;
		}

	} 

	public PluginsObserverWebView(Context context) {
        this(context, null);
    }

    public PluginsObserverWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PluginsObserverWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
        setupViews();
	}
    
    private void setupViews() {
		WebSettings settings = getSettings();
		if (settings != null) {
			settings.setJavaScriptEnabled(true);
		}
		
		setWebChromeClient(new DebugWebChromeClient());

		addJavascriptInterface(new MemoryJSPluginManager(), "plgmgr");
    }
    
	@Override
    public boolean performLongClick() {
    	return false;
    }
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		NativeObservable observable = 
			ObservableManager.getObservable(PluginObserverable.class);
		
		if (observable != null) {
			observable.addObserver(mPluginsObserver);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		NativeObservable observable = 
			ObservableManager.getObservable(PluginObserverable.class);
		
		if (observable != null) {
			observable.deleteObserver(mPluginsObserver);
		}
	}
	
	protected void onPluginsChanged() {
		
	}

	private Observer mPluginsObserver = new Observer() {
		
		@Override
		public void update(Observable observable, Object data) {
			onPluginsChanged();
		}
		
	};


}
