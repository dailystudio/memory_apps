package com.dailystudio.memory.where.fragment;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.BuildConfig;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.weather.OpenWeatherMapData;
import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;
import com.dailystudio.memory.where.weather.effects.EffectOverlayInterface;
import com.dailystudio.memory.where.weather.effects.FogEffectOverlay;
import com.dailystudio.memory.where.weather.effects.RainEffectOverlay;
import com.dailystudio.memory.where.weather.effects.SnowEffectOverlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EnvironmentFragment extends Fragment {
	
	private final static String ACTION_ENVIRONMENT_COMMAND = 
			"com.dailystudio.memory.where.ACTION_ENVIRONMENT_COMMAND";
	private final static String EXTRA_WEATHER_ID = 
			"com.dailystudio.memory.where.EXTRA_WEATHER_ID";

	private FogEffectOverlay mFogEffectOverlay;
	private SnowEffectOverlay mSnowEffectOverlay;
	private RainEffectOverlay mRainEffectOverlay;
	
	private List<EffectOverlayInterface> mEffects = 
			new ArrayList<EffectOverlayInterface>();
	
	private Weather mLastWeather;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_environment, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View view) {
		if (view == null) {
			return;
		}
		
		mFogEffectOverlay = (FogEffectOverlay) view.findViewById(R.id.fog_overlay);
		addEffect(mFogEffectOverlay);
		
		mRainEffectOverlay = (RainEffectOverlay) view.findViewById(R.id.rain_overlay);
		addEffect(mRainEffectOverlay);
		
		mSnowEffectOverlay = (SnowEffectOverlay) view.findViewById(R.id.snow_overlay);
		addEffect(mSnowEffectOverlay);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (BuildConfig.DEBUG) {
			registerCommandReceiver();
		}

		checkAndStartEffect(mLastWeather);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (BuildConfig.DEBUG) {
			unregisterCommandReceiver();
		}

		stopAllEffects();
	}
	
	public void setWeather(OpenWeatherMapData weatherData) {
		mLastWeather = null;
		
		if (weatherData == null) {
			stopAllEffects();
			return;
		}
		
		Weather w = weatherData.getMainWeather();
		if (w == null) {
			stopAllEffects();
			return;
		}

		mLastWeather = w;
		
		checkAndStartEffect(mLastWeather);
	}

	private void checkAndStartEffect(Weather w) {
		if (w == null) {
			return;
		}
		
		List<EffectOverlayInterface> effects = listEffects();
		if (effects == null) {
			return;
		}

		boolean matched = false;
		for (EffectOverlayInterface e: effects) {
			if (e == null) {
				continue;
			}
			
			matched = e.isWeatherMatched(w);
			Logger.debug("effectInterface: %s, [matched = %s]",
					e, matched);
			
			if (e.isWeatherMatched(w)) {
				e.startEffect();
			} else {
				e.stopEffect();
			}
		}
	}
	
	protected void stopAllEffects() {
		List<EffectOverlayInterface> effects = listEffects();
		if (effects == null) {
			return;
		}
		
		for (EffectOverlayInterface e: effects) {
			if (e == null) {
				continue;
			}
			
			e.stopEffect();
		}
	}
	
	protected void addEffect(EffectOverlayInterface ei) {
		if (ei == null) {
			return;
		}
		
		mEffects.add(ei);
	}
	
	protected List<EffectOverlayInterface> listEffects() {
		return new ArrayList<EffectOverlayInterface>(mEffects);
	}
	
	private void registerCommandReceiver() {
		final Context context = getActivity();
		if (context == null) {
			return;
		}

		IntentFilter filter = new IntentFilter();
		
		filter.addAction(ACTION_ENVIRONMENT_COMMAND);
	
		try {
			context.registerReceiver(mCmdReceiver, filter);
		} catch (Exception e) {
			Logger.warnning("register command receiver failure: %s",
					e.toString());
		}
	} 
	
	private void unregisterCommandReceiver() {
		final Context context = getActivity();
		if (context == null) {
			return;
		}

		try {
			context.unregisterReceiver(mCmdReceiver);
		} catch (Exception e) {
			Logger.warnning("unregister command receiver failure: %s",
					e.toString());
		}
	}

	private BroadcastReceiver mCmdReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.debug("intent = %s", intent);
			if (intent == null) {
				return;
			}
			
			final String action = intent.getAction();
			if (ACTION_ENVIRONMENT_COMMAND.equals(action)) {
				final int weatherId = intent.getIntExtra(EXTRA_WEATHER_ID, -1);
				if (weatherId == -1) {
					return;
				}
				
				Weather w = new Weather();
				
				w.id = weatherId;
				
				checkAndStartEffect(w);
			}
		}
		
	};
	
}
