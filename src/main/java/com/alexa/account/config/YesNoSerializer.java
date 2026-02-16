package com.alexa.account.config;

import com.alexa.account.util.YesNoBoolean;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Jackson serializer for Y/N boolean conversion.
 * Converts Boolean values to JSON string format (Y/N) for API responses.
 */
public class YesNoSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String result = YesNoBoolean.toYesNo(value);
        gen.writeString(result);
    }
}

