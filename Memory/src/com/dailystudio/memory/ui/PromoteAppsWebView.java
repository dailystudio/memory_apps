package com.dailystudio.memory.ui;

import com.dailystudio.app.utils.WebViewUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.promotapps.PromoteAppsInterface;

import android.content.Context;
import android.util.AttributeSet;

public class PromoteAppsWebView extends PluginsObserverWebView {
	
	private final static String LOCAL_PROMOTE_WEBPAGE = 
		"file:///android_res/raw/promote_apps.html";
	
	public PromoteAppsWebView(Context context) {
        this(context, null);
    }

    public PromoteAppsWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoteAppsWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }


    private void initMembers() {
		WebViewUtils.setBackgroundToTransparent(this);
		
		addJavascriptInterface(
    			new PromoteAppsInterface(getContext()), 
    			"promoteAppsInterface");
    	
		loadUrl(LOCAL_PROMOTE_WEBPAGE);
	}

    @Override
    protected void onPluginsChanged() {
    	super.onPluginsChanged();

    	Logger.debug("Calling webpage to checkAppsExistence()");
		loadUrl("javascript:checkAppsExistence()");
    }
	
}
