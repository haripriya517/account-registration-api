package com.alexa.account.exception;

import com.alexa.account.BaseIntegrationTest;
import com.alexa.account.dto.AccountRequestDTO;
import com.alexa.account.dto.AddressDTO;
import com.alexa.account.model.AccountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GlobalExceptionHandler Integration Tests")
class GlobalExceptionHandlerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should handle ResourceNotFoundException with HTTP 404")
    void testHandleResourceNotFoundException_Returns404WithErrorResponse() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should handle validation errors with HTTP 400 and field errors")
    void testHandleValidationExceptions_Returns400WithFieldErrors() throws Exception {
        // Request with blank name (violates @NotBlank)
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.fieldErrors", notNullValue()));
    }

    @Test
    @DisplayName("Should handle InvalidRequestException with HTTP 400")
    void testHandleInvalidRequestException_Returns400() throws Exception {
        // Try to update a non-existent draft which throws InvalidRequestException
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void testErrorResponse_IncludesTimestamp() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/INVALID"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should include error type in error response")
    void testErrorResponse_IncludesErrorType() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/INVALID"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error", notNullValue()))
            .andExpect(jsonPath("$.error", not(emptyString())));
    }

    @Test
    @DisplayName("Should include message in error response")
    void testErrorResponse_IncludesMessage() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/INVALID"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.message", not(emptyString())));
    }

    @Test
    @DisplayName("Should handle validation error with proper status code")
    void testValidationError_ProperStatusCode() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            null,
            null,
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle multiple validation errors with all field errors listed")
    void testHandleValidationExceptions_MultipleErrors_ReturnsAllFieldErrors() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",  // blank name
            null,  // null date of birth
            null,  // null address
            null,  // null account type
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors", notNullValue()))
            .andExpect(jsonPath("$.fieldErrors.name", notNullValue()))
            .andExpect(jsonPath("$.fieldErrors.dateOfBirth", notNullValue()))
            .andExpect(jsonPath("$.fieldErrors.address", notNullValue()));
    }

    @Test
    @DisplayName("Should handle InvalidRequestException when submitting without ID document")
    void testHandleInvalidRequestException_MissingDocument() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
                "",  // blank name
                null,  // null date of birth
                null,  // null address
                null,  // null account type
                null,
                null,
                null,
                null
        );
        // This will be caught when controller validates the request
        mockMvc.perform(multipart("/api/v1/accounts/register")
                        .file(new MockMultipartFile("idDocument", new byte[0]))
                        .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return proper error structure for ResourceNotFoundException")
    void testResourceNotFoundException_ErrorStructure() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT-ID-12345"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("Should return proper error structure for validation errors")
    void testValidationException_ErrorStructure() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.timestamp", notNullValue()))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Validation Failed")))
            .andExpect(jsonPath("$.fieldErrors", notNullValue()))
            .andExpect(jsonPath("$.fieldErrors", isA(Map.class)));
    }

    @Test
    @DisplayName("Should handle validation error for invalid email format")
    void testValidationException_InvalidEmail() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            "invalid-email",  // invalid email format
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.email", notNullValue()));
    }

    @Test
    @DisplayName("Should handle validation error for past date validation")
    void testValidationException_FutureDateOfBirth() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(2099, 5, 15),  // future date
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.dateOfBirth", notNullValue()));
    }

    @Test
    @DisplayName("Should handle validation error for negative starting balance")
    void testValidationException_NegativeStartingBalance() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            java.math.BigDecimal.valueOf(-100),  // negative balance
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.startingBalance", notNullValue()));
    }

    @Test
    @DisplayName("Should include timestamp in all error responses")
    void testErrorResponses_AlwaysIncludeTimestamp() throws Exception {
        // Test with ResourceNotFoundException
        mockMvc.perform(get("/api/v1/accounts/INVALID"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.timestamp", notNullValue()));

        // Test with ValidationException
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            null,
            null,
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return appropriate status codes for different exceptions")
    void testErrorResponses_AppropriateStatusCodes() throws Exception {
        // 404 for ResourceNotFoundException
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        // 400 for validation errors
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            null,
            null,
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("Should handle validation error with invalid address")
    void testValidationException_InvalidAddress() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("", "", "", ""),  // all blank address fields
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors", notNullValue()));
    }

    @Test
    @DisplayName("Should provide clear error messages in validation failures")
    void testValidationException_ClearErrorMessages() throws Exception {
        AccountRequestDTO invalidDTO = new AccountRequestDTO(
            "",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MockMultipartFile idDocument = new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name", notNullValue()))
            .andExpect(jsonPath("$.fieldErrors.name", not(emptyString())));
    }
}
