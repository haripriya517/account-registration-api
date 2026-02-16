package com.alexa.account.service;

import com.alexa.account.dto.FieldValidationRequest;
import com.alexa.account.dto.FieldValidationResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for field validation.
 * Defines contracts for real-time field validation feedback.
 */
public interface IFieldValidationService {

    /**
     * Validate a single field for frontend real-time validation.
     *
     * @param request the field validation request containing field name and value
     * @return FieldValidationResponse with validation result and message
     */
    FieldValidationResponse validateField(FieldValidationRequest request);

    /**
     * Validate name field.
     *
     * @param value the name value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateName(String value);

    /**
     * Validate date of birth field.
     *
     * @param value the date of birth value to validate (DD-MM-YYYY format)
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateDateOfBirth(String value);

    /**
     * Validate street name field.
     *
     * @param value the street name value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateStreetName(String value);

    /**
     * Validate house number field.
     *
     * @param value the house number value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateHouseNumber(String value);

    /**
     * Validate post code field.
     *
     * @param value the post code value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validatePostCode(String value);

    /**
     * Validate city field.
     *
     * @param value the city value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateCity(String value);

    /**
     * Validate account type field.
     *
     * @param value the account type value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateAccountType(String value);

    /**
     * Validate ID document file.
     *
     * @param idDocument the document file to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateIdDocument(MultipartFile idDocument);

    /**
     * Validate interested in other products field (Y/N).
     *
     * @param value the Y/N value to validate
     * @return FieldValidationResponse with validation result
     */
    FieldValidationResponse validateInterestedInOtherProducts(String value);
}

