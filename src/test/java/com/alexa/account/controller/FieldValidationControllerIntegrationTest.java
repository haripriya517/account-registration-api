package com.alexa.account.controller;

import com.alexa.account.BaseIntegrationTest;
import com.alexa.account.dto.FieldValidationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("FieldValidationController Integration Tests")
class FieldValidationControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should validate street name endpoint with HTTP 200")
    void testValidateStreetName_ValidInput_Returns200() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("streetname", "Main Street");

        mockMvc.perform(post("/api/v1/validation/streetName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @DisplayName("Should validate city endpoint with HTTP 200")
    void testValidateCity_ValidInput_Returns200() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("city", "Amsterdam");

        mockMvc.perform(post("/api/v1/validation/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true));
    }


    @Test
    @DisplayName("Should validate valid house number with HTTP 200")
    void testValidateHouseNumber_ValidNumber_Returns200() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("houseNumber", "123");

        mockMvc.perform(post("/api/v1/validation/houseNumber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    // Additional Name Validation Tests
    @Test
    @DisplayName("Should reject null name via endpoint")
    void testValidateName_NullValue_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("name", "");

        mockMvc.perform(post("/api/v1/validation/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.message", containsString("mandatory")));
    }


    @Test
    @DisplayName("Should reject postcode without space")
    void testValidatePostCode_NoSpace_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("postcode", "1234AB");

        mockMvc.perform(post("/api/v1/validation/postCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.message", containsString("space")));
    }

    // Street Name Validation Tests
    @Test
    @DisplayName("Should reject blank street name")
    void testValidateStreetName_Blank_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("streetname", "   ");

        mockMvc.perform(post("/api/v1/validation/streetName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.message", containsString("mandatory")));
    }

    // City Validation Tests
    @Test
    @DisplayName("Should reject blank city")
    void testValidateCity_Blank_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("city", "");

        mockMvc.perform(post("/api/v1/validation/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false))
            .andExpect(jsonPath("$.message", containsString("mandatory")));
    }


    // Date of Birth Validation Tests
    @Test
    @DisplayName("Should reject future date of birth")
    void testValidateDateOfBirth_FutureDate_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("dateofbirth", "15-05-2099");

        mockMvc.perform(post("/api/v1/validation/dateOfBirth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message", containsString("past")));
    }

    @Test
    @DisplayName("Should reject invalid date format")
    void testValidateDateOfBirth_InvalidFormat_ReturnsFalse() throws Exception {
        FieldValidationRequest request = new FieldValidationRequest("dateofbirth", "2020-05-15");

        mockMvc.perform(post("/api/v1/validation/dateOfBirth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message", containsString("DD-MM-YYYY")));
    }

}

