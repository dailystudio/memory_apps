package com.dailystudio.memory.where.weather;

import java.util.Locale;

import com.dailystudio.development.Logger;
import com.dg.libs.rest.callbacks.HttpCallback;
import com.dg.libs.rest.client.RequestMethod;
import com.dg.libs.rest.requests.RestClientRequest;

public class OpenWeatherMapWeatherRequest extends RestClientRequest<OpenWeatherMapData> {

	public OpenWeatherMapWeatherRequest(double lat, double lon,
    		HttpCallback<OpenWeatherMapData> callback) {
		super();

		setRequestMethod(RequestMethod.GET);
		setUrl(OpenWeatherMapContants.WEATHER_REQUEST_URL);
		setParser(new OpenWeatherMapParser());
		setCallback(callback);


		addQueryParam(OpenWeatherMapContants.PARAM_LATITUDE, String.valueOf(lat));
		addQueryParam(OpenWeatherMapContants.PARAM_LONGITUDE, String.valueOf(lon));
		addQueryParam(OpenWeatherMapContants.PARAM_UNITS, buildTempUnit());
        
    	String code = buildLangCode();
    	Logger.debug("code = %s", code);
    	if (code != null) {
			addQueryParam(OpenWeatherMapContants.PARAM_LANGUAGE, code);
//    		addParam(OpenWeatherMapContants.PARAM_LANGUAGE, "en_us");
    	}
    }
    
	private String buildTempUnit() {
		return OpenWeatherMapContants.TEMPERATURE_UNIT_METRIC;
	}
	
    private String buildLangCode() {
        Locale locale = Locale.getDefault();
        if (locale == null) {
        	return null;
        }
        
    	String lang = locale.getLanguage();
    	if (lang == null) {
    		return null;
    	}
    	
    	String country = locale.getCountry();
    	if (country == null) {
    		return lang;
    	}
    	
    	return String.format("%s_%s", lang, country.toLowerCase());
    }

}
