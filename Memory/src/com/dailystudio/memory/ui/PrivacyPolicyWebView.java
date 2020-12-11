package com.dailystudio.memory.ui;

import android.content.Context;
import android.util.AttributeSet;

public class PrivacyPolicyWebView extends PluginsObserverWebView {
	
	private final static String LOCAL_PRIVACY_POLICY_WEBPAGE = 
		"file:///android_res/raw/privay_policy.html";
	
	public PrivacyPolicyWebView(Context context) {
        this(context, null);
    }

    public PrivacyPolicyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivacyPolicyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
		loadUrl(LOCAL_PRIVACY_POLICY_WEBPAGE);
    }
    
	@Override
	protected void onPluginsChanged() {
		super.onPluginsChanged();

		/*
		 * XXX: on Android 4.1 Nexus 7
		 * 		javascript:loadPluginsPrivacy() will cause webview not
		 * 		refresh correctly, so just refresh whole page
		 */
//		loadUrl("javascript:loadPluginsPrivacy()");
		loadUrl(LOCAL_PRIVACY_POLICY_WEBPAGE);
	}
	
}
