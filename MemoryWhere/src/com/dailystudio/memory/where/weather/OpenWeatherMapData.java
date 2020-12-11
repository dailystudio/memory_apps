package com.dailystudio.memory.where.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenWeatherMapData {

	public static class Weather {
		
		@JsonProperty
		public int id;
		@JsonProperty
		public String main;
		@JsonProperty
		public String description;
		@JsonProperty
		public String icon;
		
		@Override
		public String toString() {
			return String.format("id[%d], weather[%s, %s, %s]",
					id, main, description, icon);
		}
		
	}
	
	public static class Coord {
		
		@JsonProperty
		public double lat;
		@JsonProperty
		public double lon;
		
		@Override
		public String toString() {
			return String.format("%3.2f, %3.2f", lat, lon);
		}
		
	}
	
	public static class MainInfo {

		@JsonProperty
		public float temp;
		@JsonProperty
		public float temp_min;
		@JsonProperty
		public float temp_max;
		
		@Override
		public String toString() {
			return String.format("%3.1f, (%3.1f - %3.1f)",
					temp, temp_min, temp_max);
		}
		
	}
	
	@JsonProperty
	public String name;
	@JsonProperty
	public Coord coord;
	@JsonProperty
	public Weather[] weather;
	@JsonProperty
	public MainInfo main;

	public Weather getMainWeather() {
		if (weather == null || weather.length <= 0) {
			return null;
		}
		
		return weather[0];
	}

	public String dumpWeathers() {
		if (weather == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (Weather w: weather) {
			builder.append('<');
			builder.append(w);
			builder.append('>');
		}
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): City[%s], Coord[%s], Main[%s], Weather[%s]",
				getClass().getSimpleName(),
				hashCode(),
				name,
				coord, 
				main,
				dumpWeathers());
	}
	
}
