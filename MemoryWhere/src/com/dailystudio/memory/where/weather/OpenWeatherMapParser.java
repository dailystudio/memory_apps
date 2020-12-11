package com.dailystudio.memory.where.weather;

import java.io.InputStream;

import com.dg.libs.rest.parsers.BaseJacksonMapperResponseParser;

public class OpenWeatherMapParser
	extends BaseJacksonMapperResponseParser<OpenWeatherMapData> {

    @Override
    public OpenWeatherMapData parse(InputStream instream) throws Exception {
        return mapper.readValue(instream, OpenWeatherMapData.class);
    }
    
}
