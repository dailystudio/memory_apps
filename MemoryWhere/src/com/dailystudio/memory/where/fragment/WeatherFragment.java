package com.dailystudio.memory.where.fragment;

import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.weather.OpenWeatherMapData;
import com.dailystudio.memory.where.weather.OpenWeatherMapData.Weather;
import com.dailystudio.memory.where.weather.WeatherIcon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherFragment extends Fragment {

	private TextView mWeatherDescription;
	private TextView mWeatherCurrentTemp;
	private TextView mWeatherTemperature;
	private ImageView mWeatherIcon;
	
	private View mWeatherInfo;
	private View mWeatherLoading;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_weather, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View view) {
		if (view == null) {
			return;
		}
		
		mWeatherDescription = 
				(TextView) view.findViewById(R.id.weather_description);
		mWeatherCurrentTemp = 
				(TextView) view.findViewById(R.id.weather_current_temp);
		mWeatherTemperature = 
				(TextView) view.findViewById(R.id.weather_temperature);
		mWeatherIcon = 
				(ImageView) view.findViewById(R.id.weather_icon);
		
		mWeatherInfo = view.findViewById(R.id.weather_info);
		mWeatherLoading = view.findViewById(R.id.weather_loading);
	}

	public void setWeather(OpenWeatherMapData weatherData) {
		if (weatherData == null) {
			reset();
		}
		
		if (mWeatherInfo != null) {
			mWeatherInfo.setVisibility(View.VISIBLE);
		}
		
		if (mWeatherLoading != null) {
			mWeatherLoading.setVisibility(View.INVISIBLE);
		}
		
		Weather weather = weatherData.getMainWeather();
		if (weather == null) {
			return;
		}
		
		if (mWeatherDescription != null) {
			mWeatherDescription.setText(weather.description);
		}
		
		final String unit = getString(R.string.temperatur_unit_metric);
		
		if (mWeatherCurrentTemp != null) {
			final String temp = String.format("%d%s",
					(int)Math.round(weatherData.main.temp),
					unit);
			mWeatherCurrentTemp.setText(temp);
		}
		
		if (mWeatherTemperature != null) {
			final String temp = String.format("%d%s / %d%s", 
					(int)Math.round(weatherData.main.temp_min),
					unit,
					(int)Math.round(weatherData.main.temp_max),
					unit);
			mWeatherTemperature.setText(temp);
		}
		
		if (mWeatherIcon != null) {
			mWeatherIcon.setImageDrawable(WeatherIcon.getIcon(
					getActivity(), WeatherIcon.PACK_CLIMACONS, weather.id));
		}
	}

	public void reset() {
		if (mWeatherInfo != null) {
			mWeatherInfo.setVisibility(View.INVISIBLE);
		}
		
		if (mWeatherLoading != null) {
			mWeatherLoading.setVisibility(View.VISIBLE);
		}
	}
	
}
