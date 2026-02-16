package com.alexa.account.dto;

public record FieldValidationResponse(
        boolean valid,
        String message
) {
}

