package com.alexa.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RequestIdGeneratorService Tests")
class RequestIdGeneratorServiceTest {

    private RequestIdGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new RequestIdGeneratorService();
    }

    @Test
    @DisplayName("Should generate request ID with format XXXX-YYYY")
    void testGenerateRequestId_ValidDate_ReturnsFormattedId() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        String result = service.generateRequestId(dob);

        assertNotNull(result);
        assertTrue(result.matches("[A-Z2-9]{4}-\\d{4}"), "Request ID should match pattern XXXX-YYYY");
    }

    @Test
    @DisplayName("Should generate different IDs for same DOB")
    void testGenerateRequestId_SameDob_DifferentIds() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        String id1 = service.generateRequestId(dob);
        String id2 = service.generateRequestId(dob);

        String dob1Part = id1.substring(5);
        String dob2Part = id2.substring(5);

        assertEquals(dob1Part, dob2Part, "DOB part should be same");
        assertNotEquals(id1, id2, "Random part should differ");
    }

    @Test
    @DisplayName("Should include correct DOB month-year in request ID")
    void testGenerateRequestId_CorrectDobFormat() {
        LocalDate dob = LocalDate.of(2025, 2, 15);
        String result = service.generateRequestId(dob);

        assertTrue(result.endsWith("0225"), "Should end with month-year (0225 for Feb 2025)");
    }

    @Test
    @DisplayName("Should handle null DOB")
    void testGenerateRequestId_NullDob_Returns0000AsDefault() {
        String result = service.generateRequestId(null);

        assertNotNull(result);
        assertTrue(result.endsWith("0000"), "Should use 0000 for null DOB");
    }

    @Test
    @DisplayName("Should generate 9-character request ID (XXXX-YYYY with hyphen)")
    void testGenerateRequestId_Length() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        String result = service.generateRequestId(dob);

        assertEquals(9, result.length(), "Request ID should be 9 characters (XXXX-YYYY with hyphen)");
    }

    @Test
    @DisplayName("Should only use readable characters (no I, O, 0, 1)")
    void testGenerateRequestId_ReadableCharsOnly() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        String result = service.generateRequestId(dob);
        String randomPart = result.substring(0, 4);

        assertFalse(randomPart.contains("I"), "Should not contain I");
        assertFalse(randomPart.contains("O"), "Should not contain O");
        assertFalse(randomPart.contains("0"), "Should not contain 0");
        assertFalse(randomPart.contains("1"), "Should not contain 1");
    }

    @Test
    @DisplayName("Should generate valid IDs multiple times")
    void testGenerateRequestId_MultipleGenerations_AllValid() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        for (int i = 0; i < 10; i++) {
            String result = service.generateRequestId(dob);
            assertTrue(result.matches("[A-Z2-9]{4}-0590"), "All generated IDs should be valid");
        }
    }
}

