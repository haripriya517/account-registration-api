package com.alexa.account.config;

import com.alexa.account.util.YesNoBoolean;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * Jackson deserializer for Y/N boolean conversion.
 * Converts JSON string values (Y/N/YES/NO) to Boolean for DTOs.
 */
public class YesNoDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        try {
            return YesNoBoolean.toBoolean(value);
        } catch (IllegalArgumentException e) {
            throw ctxt.instantiationException(Boolean.class, e.getMessage());
        }
    }
}

