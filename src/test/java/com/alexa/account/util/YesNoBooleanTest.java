package com.alexa.account.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YesNoBoolean Utility Tests")
class YesNoBooleanTest {

    @Test
    @DisplayName("Should convert true to 'Y'")
    void testToYesNo_True_ReturnsY() {
        String result = YesNoBoolean.toYesNo(true);
        assertEquals("Y", result);
    }

    @Test
    @DisplayName("Should convert false to 'N'")
    void testToYesNo_False_ReturnsN() {
        String result = YesNoBoolean.toYesNo(false);
        assertEquals("N", result);
    }

    @Test
    @DisplayName("Should return null for null input")
    void testToYesNo_Null_ReturnsNull() {
        String result = YesNoBoolean.toYesNo(null);
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Y", "YES", "yes", "True", "TRUE", "1", "ON", "on"})
    @DisplayName("Should convert various yes values to true")
    void testToBoolean_YesValues_ReturnsTrue(String value) {
        Boolean result = YesNoBoolean.toBoolean(value);
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "NO", "no", "False", "FALSE", "0", "OFF", "off"})
    @DisplayName("Should convert various no values to false")
    void testToBoolean_NoValues_ReturnsFalse(String value) {
        Boolean result = YesNoBoolean.toBoolean(value);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return null for null input in toBoolean")
    void testToBoolean_Null_ReturnsNull() {
        Boolean result = YesNoBoolean.toBoolean(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should throw exception for invalid value")
    void testToBoolean_InvalidValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            YesNoBoolean.toBoolean("INVALID");
        });
    }

    @Test
    @DisplayName("Should validate Y as valid")
    void testIsValidYesNoValue_Y_ReturnsTrue() {
        assertTrue(YesNoBoolean.isValidYesNoValue("Y"));
    }

    @Test
    @DisplayName("Should validate N as valid")
    void testIsValidYesNoValue_N_ReturnsTrue() {
        assertTrue(YesNoBoolean.isValidYesNoValue("N"));
    }

    @Test
    @DisplayName("Should validate YES as valid")
    void testIsValidYesNoValue_YES_ReturnsTrue() {
        assertTrue(YesNoBoolean.isValidYesNoValue("YES"));
    }

    @Test
    @DisplayName("Should validate null as valid (optional field)")
    void testIsValidYesNoValue_Null_ReturnsTrue() {
        assertTrue(YesNoBoolean.isValidYesNoValue(null));
    }

    @Test
    @DisplayName("Should reject invalid value")
    void testIsValidYesNoValue_InvalidValue_ReturnsFalse() {
        assertFalse(YesNoBoolean.isValidYesNoValue("INVALID"));
    }

    @Test
    @DisplayName("Should handle whitespace in input")
    void testToBoolean_WithWhitespace_TrimsAndConverts() {
        Boolean result = YesNoBoolean.toBoolean("  Y  ");
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle mixed case")
    void testToBoolean_MixedCase_Converts() {
        Boolean result = YesNoBoolean.toBoolean("YeS");
        assertTrue(result);
    }
}

