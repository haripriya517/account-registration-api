package com.alexa.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

@Configuration
public class MultipartJacksonConfig {

    /**
     * Configure Jackson to handle application/octet-stream for multipart requests.
     * This allows @RequestPart to deserialize JSON even when content-type is not explicitly set.
     */
    public MultipartJacksonConfig(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }
}

