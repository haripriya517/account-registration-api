package com.alexa.account.dto;

public record FieldValidationRequest(
        String fieldName,
        String fieldValue
) {
}

