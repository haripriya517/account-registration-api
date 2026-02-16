package com.alexa.account.dto;

public record IdDocumentResponseDTO(
        String documentName,
        String documentType,
        long documentSize
) {
}

