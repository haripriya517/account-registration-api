package com.alexa.account.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YesNoConverter Tests")
class YesNoConverterTest {

    private YesNoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new YesNoConverter();
    }

    @Test
    @DisplayName("Should convert true to Y for database")
    void testConvertToDatabaseColumn_True_ReturnsY() {
        String result = converter.convertToDatabaseColumn(true);
        assertEquals("Y", result);
    }

    @Test
    @DisplayName("Should convert false to N for database")
    void testConvertToDatabaseColumn_False_ReturnsN() {
        String result = converter.convertToDatabaseColumn(false);
        assertEquals("N", result);
    }

    @Test
    @DisplayName("Should return null for null input")
    void testConvertToDatabaseColumn_Null_ReturnsNull() {
        String result = converter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert Y from database to true")
    void testConvertToEntityAttribute_Y_ReturnsTrue() {
        Boolean result = converter.convertToEntityAttribute("Y");
        assertTrue(result);
    }

    @Test
    @DisplayName("Should convert N from database to false")
    void testConvertToEntityAttribute_N_ReturnsFalse() {
        Boolean result = converter.convertToEntityAttribute("N");
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return null for null database value")
    void testConvertToEntityAttribute_Null_ReturnsNull() {
        Boolean result = converter.convertToEntityAttribute(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle round-trip conversion true to Y to true")
    void testRoundTrip_TrueToYToTrue() {
        String dbValue = converter.convertToDatabaseColumn(true);
        Boolean result = converter.convertToEntityAttribute(dbValue);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle round-trip conversion false to N to false")
    void testRoundTrip_FalseToNToFalse() {
        String dbValue = converter.convertToDatabaseColumn(false);
        Boolean result = converter.convertToEntityAttribute(dbValue);
        assertFalse(result);
    }
}

