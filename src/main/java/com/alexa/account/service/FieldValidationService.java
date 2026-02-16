package com.alexa.account.service;

import com.alexa.account.dto.FieldValidationRequest;
import com.alexa.account.dto.FieldValidationResponse;
import com.alexa.account.util.YesNoBoolean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class FieldValidationService implements IFieldValidationService {

    private static final Pattern POSTCODE_PATTERN = Pattern.compile("^[0-9]{4}\\s[A-Za-z]{2}$");
    private static final Pattern HOUSE_NUMBER_PATTERN = Pattern.compile("^[1-9][0-9]{0,4}([A-Za-z])?(-[A-Za-z0-9]+)?$");
    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Validate a single field for frontend real-time validation.
     */
    @Override
    public FieldValidationResponse validateField(FieldValidationRequest request) {
        String fieldName = request.fieldName();
        String fieldValue = request.fieldValue();

        return switch (fieldName.toLowerCase()) {
            case "name" -> validateName(fieldValue);
            case "dateofbirth" -> validateDateOfBirth(fieldValue);
            case "streetname" -> validateStreetName(fieldValue);
            case "housenumber" -> validateHouseNumber(fieldValue);
            case "postcode" -> validatePostCode(fieldValue);
            case "city" -> validateCity(fieldValue);
            case "accounttype" -> validateAccountType(fieldValue);
            case "interestedinotherprodcts" -> validateInterestedInOtherProducts(fieldValue);
            default -> new FieldValidationResponse(false, "Unknown field: " + fieldName);
        };
    }

    @Override
    public FieldValidationResponse validateName(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "Name is mandatory");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validateDateOfBirth(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "Date of birth is mandatory");
        }
        try {
            LocalDate dob = LocalDate.parse(value, DOB_FORMATTER);
            if (!dob.isBefore(LocalDate.now())) {
                return new FieldValidationResponse(false, "Date of birth must be in the past");
            }
            return new FieldValidationResponse(true, "Valid");
        } catch (Exception e) {
            return new FieldValidationResponse(false, "Invalid date format. Use DD-MM-YYYY (e.g., 15-05-1990)");
        }
    }

    @Override
    public FieldValidationResponse validateStreetName(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "Street name is mandatory");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validateHouseNumber(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "House number is mandatory");
        }
        if (!HOUSE_NUMBER_PATTERN.matcher(value).matches()) {
            return new FieldValidationResponse(false, "House number must be a valid Dutch house number (e.g., 123, 45A, 7-1, 123-bis)");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validatePostCode(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "Post code is mandatory");
        }
        if (!POSTCODE_PATTERN.matcher(value).matches()) {
            return new FieldValidationResponse(false, "Post code must be 4 digits followed by space and 2 alphabets (e.g., 1234 AB)");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validateCity(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "City is mandatory");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validateAccountType(String value) {
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(false, "Account type is mandatory");
        }
        if (!isValidAccountType(value)) {
            return new FieldValidationResponse(false, "Account type must be one of: Savings, Current, Investment");
        }
        return new FieldValidationResponse(true, "Valid");
    }

    @Override
    public FieldValidationResponse validateIdDocument(MultipartFile idDocument) {
        if (idDocument == null || idDocument.isEmpty()) {
            return new FieldValidationResponse(false, "ID document is mandatory");
        }

        String contentType = idDocument.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            return new FieldValidationResponse(false, "ID document must be an image (JPG, PNG) or PDF");
        }

        // Check file size (max 10MB)
        if (idDocument.getSize() > 10 * 1024 * 1024) {
            return new FieldValidationResponse(false, "ID document must be less than 10MB");
        }

        return new FieldValidationResponse(true, "Valid");
    }

    /**
     * Validate if the account type is one of the allowed types.
     */
    private boolean isValidAccountType(String accountType) {
        return accountType != null && (accountType.equalsIgnoreCase("Savings") ||
                                       accountType.equalsIgnoreCase("Current") ||
                                       accountType.equalsIgnoreCase("Investment"));
    }

    @Override
    public FieldValidationResponse validateInterestedInOtherProducts(String value) {
        // Optional field
        if (value == null || value.isBlank()) {
            return new FieldValidationResponse(true, "Valid");
        }

        if (!YesNoBoolean.isValidYesNoValue(value)) {
            return new FieldValidationResponse(false, "Value must be Y or N (case-insensitive)");
        }
        return new FieldValidationResponse(true, "Valid");
    }
}
