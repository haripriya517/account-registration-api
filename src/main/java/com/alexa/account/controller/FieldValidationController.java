package com.alexa.account.controller;

import com.alexa.account.dto.FieldValidationRequest;
import com.alexa.account.dto.FieldValidationResponse;
import com.alexa.account.service.FieldValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Field Validation Controller - API Version 1
 * Provides real-time field validation endpoints for frontend.
 */
@RestController
@RequestMapping("/api/v1/validation")
@RequiredArgsConstructor
public class FieldValidationController {

    private final FieldValidationService fieldValidationService;

    /**
     * Validate name field.
     */
    @PostMapping("/name")
    public ResponseEntity<FieldValidationResponse> validateName(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate date of birth field.
     */
    @PostMapping("/dateOfBirth")
    public ResponseEntity<FieldValidationResponse> validateDateOfBirth(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate street name field.
     */
    @PostMapping("/streetName")
    public ResponseEntity<FieldValidationResponse> validateStreetName(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate house number field.
     */
    @PostMapping("/houseNumber")
    public ResponseEntity<FieldValidationResponse> validateHouseNumber(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate post code field.
     */
    @PostMapping("/postCode")
    public ResponseEntity<FieldValidationResponse> validatePostCode(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate city field.
     */
    @PostMapping("/city")
    public ResponseEntity<FieldValidationResponse> validateCity(@RequestBody FieldValidationRequest request) {
        FieldValidationResponse response = fieldValidationService.validateField(request);
        return ResponseEntity.ok(response);
    }

}
