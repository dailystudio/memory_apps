package com.dailystudio.memory.where.weather.effects;

import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;

public interface EffectOverlayInterface {

	public boolean isWeatherMatched(Weather w);
	public void startEffect();
	public void stopEffect();
	
}
