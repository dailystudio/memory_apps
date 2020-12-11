package com.dailystudio.memory.where.weather.effects;

import com.dailystudio.memory.where.ui.OverlayWebView;
import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;

import android.content.Context;
import android.util.AttributeSet;

public class RainEffectOverlay extends OverlayWebView implements EffectOverlayInterface {
	
	public RainEffectOverlay(Context context) {
        this(context, null);
    }

    public RainEffectOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initMembers();
    }

    public RainEffectOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initMembers();
    }
    
    private void initMembers() {
    	setVisibility(INVISIBLE);
    }

	@Override
	public boolean isWeatherMatched(Weather w) {
		if (w == null) {
			return false;
		}
		
//		return true;
		return (w.id / 100 == 2 
				|| w.id / 100 == 3
				|| w.id / 100 == 5);
	}

	@Override
	public void startEffect() {
		loadUrl("file:///android_asset/rain.html");
		setVisibility(VISIBLE);
	}

	@Override
	public void stopEffect() {
		setVisibility(GONE);
		loadUrl("about:blank");
	}
    
}
