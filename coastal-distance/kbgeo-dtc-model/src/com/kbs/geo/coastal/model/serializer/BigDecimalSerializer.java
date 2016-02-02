package com.kbs.geo.coastal.model.serializer;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
	
    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException, JsonProcessingException {
    	jsonGenerator.writeStartObject();
    	jsonGenerator.writeNumber((null == value ? null : value.doubleValue()));
    	jsonGenerator.writeEndObject();
    }
}
