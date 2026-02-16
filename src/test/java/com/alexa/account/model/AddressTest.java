package com.alexa.account.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Model Tests")
class AddressTest {

    @Test
    @DisplayName("Should create address with all constructor parameters")
    void testAddressConstructor_AllFields_Success() {
        Address address = new Address("Main St", "123", "1234 AB", "Amsterdam");

        assertEquals("Main St", address.getStreetName());
        assertEquals("123", address.getHouseNumber());
        assertEquals("1234 AB", address.getPostCode());
        assertEquals("Amsterdam", address.getCity());
    }

    @Test
    @DisplayName("Should create address with no-arg constructor")
    void testAddressNoArgConstructor_Success() {
        Address address = new Address();
        assertNotNull(address);
    }

    @Test
    @DisplayName("Should set and get street name")
    void testSetGetStreetName_Success() {
        Address address = new Address();
        address.setStreetName("Main St");
        assertEquals("Main St", address.getStreetName());
    }

    @Test
    @DisplayName("Should set and get house number")
    void testSetGetHouseNumber_Success() {
        Address address = new Address();
        address.setHouseNumber("123");
        assertEquals("123", address.getHouseNumber());
    }

    @Test
    @DisplayName("Should set and get post code")
    void testSetGetPostCode_Success() {
        Address address = new Address();
        address.setPostCode("1234 AB");
        assertEquals("1234 AB", address.getPostCode());
    }

    @Test
    @DisplayName("Should set and get city")
    void testSetGetCity_Success() {
        Address address = new Address();
        address.setCity("Amsterdam");
        assertEquals("Amsterdam", address.getCity());
    }

    @Test
    @DisplayName("Should handle multiple set operations")
    void testMultipleSetOperations_Success() {
        Address address = new Address();

        address.setStreetName("Keizersgracht");
        address.setHouseNumber("45B");
        address.setPostCode("1015 AB");
        address.setCity("Amsterdam");

        assertEquals("Keizersgracht", address.getStreetName());
        assertEquals("45B", address.getHouseNumber());
        assertEquals("1015 AB", address.getPostCode());
        assertEquals("Amsterdam", address.getCity());
    }

    @Test
    @DisplayName("Should allow null values in fields")
    void testNullValues_Success() {
        Address address = new Address(null, null, null, null);

        assertNull(address.getStreetName());
        assertNull(address.getHouseNumber());
        assertNull(address.getPostCode());
        assertNull(address.getCity());
    }
}

