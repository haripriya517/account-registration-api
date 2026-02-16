package com.alexa.account.controller;

import com.alexa.account.BaseIntegrationTest;
import com.alexa.account.dto.AccountRequestDTO;
import com.alexa.account.dto.AddressDTO;
import com.alexa.account.dto.DraftRequestDTO;
import com.alexa.account.model.AccountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AccountController Integration Tests")
class AccountControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile createMockFile() {
        return new MockMultipartFile(
            "idDocument",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );
    }

    private AccountRequestDTO createValidAccountRequest() {
        return new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );
    }

    private DraftRequestDTO createValidDraftRequest() {
        return new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );
    }

    @Test
    @DisplayName("Should register account successfully with HTTP 201")
    void testRegister_ValidRequest_Returns201AndResponse() throws Exception {
        AccountRequestDTO requestDTO = createValidAccountRequest();
        MockMultipartFile idDocument = createMockFile();

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(requestDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.requestId", notNullValue()))
            .andExpect(jsonPath("$.name").value("Haripriya"));
    }

    @Test
    @DisplayName("Should save draft successfully with HTTP 201")
    void testSaveDraft_ValidRequest_Returns201AndResponse() throws Exception {
        DraftRequestDTO requestDTO = createValidDraftRequest();

        mockMvc.perform(multipart("/api/v1/accounts/draft")
                .part(new MockPart("request", objectMapper.writeValueAsString(requestDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.requestId", notNullValue()))
            .andExpect(jsonPath("$.name").value("Haripriya"));
    }

    @Test
    @DisplayName("Should save draft with optional document")
    void testSaveDraft_WithDocument_Returns201() throws Exception {
        DraftRequestDTO requestDTO = createValidDraftRequest();
        MockMultipartFile idDocument = createMockFile();

        mockMvc.perform(multipart("/api/v1/accounts/draft")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(requestDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.requestId", notNullValue()));
    }

    @Test
    @DisplayName("Should get account by request ID with HTTP 200")
    void testGetByRequestId_ExistingRequestId_Returns200() throws Exception {
        // First register an account
        AccountRequestDTO requestDTO = createValidAccountRequest();
        MockMultipartFile idDocument = createMockFile();

        MvcResult registerResult = mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(requestDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        String requestId = objectMapper.readTree(responseBody).get("requestId").asText();

        // Then retrieve it
        mockMvc.perform(get("/api/v1/accounts/{requestId}", requestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Haripriya"))
            .andExpect(jsonPath("$.requestId").value(requestId));
    }

    @Test
    @DisplayName("Should get non-existent account with HTTP 404")
    void testGetByRequestId_NonExistentRequestId_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update draft successfully with HTTP 200")
    void testUpdateDraft_ValidRequest_Returns200() throws Exception {
        // First save draft
        DraftRequestDTO draftDTO = createValidDraftRequest();
        MvcResult draftResult = mockMvc.perform(multipart("/api/v1/accounts/draft")
                .part(new MockPart("request", objectMapper.writeValueAsString(draftDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andReturn();

        String draftResponseBody = draftResult.getResponse().getContentAsString();
        String requestId = objectMapper.readTree(draftResponseBody).get("requestId").asText();

        // Update draft
        AccountRequestDTO updateDTO = new AccountRequestDTO(
            "Haripriya Updated",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(multipart("/api/v1/accounts/{requestId}", requestId)
                .part(new MockPart("request", objectMapper.writeValueAsString(updateDTO).getBytes()))
                .with(req -> {
                    req.setMethod("PUT");
                    return req;
                })
                .contentType("multipart/form-data"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Haripriya Updated"));
    }

    @Test
    @DisplayName("Should submit draft successfully with HTTP 200")
    void testSubmitDraft_ValidRequest_Returns200() throws Exception {
        // First save draft
        DraftRequestDTO draftDTO = createValidDraftRequest();
        MvcResult draftResult = mockMvc.perform(multipart("/api/v1/accounts/draft")
                .file(createMockFile())
                .part(new MockPart("request", objectMapper.writeValueAsString(draftDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andReturn();

        String draftResponseBody = draftResult.getResponse().getContentAsString();
        String requestId = objectMapper.readTree(draftResponseBody).get("requestId").asText();

        // Submit draft using combined endpoint with requestId parameter
        AccountRequestDTO submitDTO = createValidAccountRequest();

        MockMultipartFile requestPart = new MockMultipartFile(
            "request",
            "",
            "application/json",
            objectMapper.writeValueAsString(submitDTO).getBytes()
        );

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(requestPart)
                .param("requestId", requestId)
                .contentType("multipart/form-data"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUBMITTED"))
            .andExpect(jsonPath("$.requestId").value(requestId))
            // Verify that the submitted request data is returned in the response
            .andExpect(jsonPath("$.name").value("Haripriya"))
            .andExpect(jsonPath("$.dateOfBirth").value("15-05-1990"))
            .andExpect(jsonPath("$.address.streetName").value("Main Street"))
            .andExpect(jsonPath("$.address.houseNumber").value("123"))
            .andExpect(jsonPath("$.address.postCode").value("1234 AB"))
            .andExpect(jsonPath("$.address.city").value("Amsterdam"))
            .andExpect(jsonPath("$.accountType").value("SAVINGS"));
    }

    @Test
    @DisplayName("Should submit draft via /register with requestId and return same requestId with updated data")
    void testRegisterWithRequestId_SubmitDraft_ReturnsSameRequestIdWithUpdatedData() throws Exception {
        // Step 1: Save draft with initial data (Haripriya)
        DraftRequestDTO draftDTO = createValidDraftRequest();
        MvcResult draftResult = mockMvc.perform(multipart("/api/v1/accounts/draft")
                .file(createMockFile())
                .part(new MockPart("request", objectMapper.writeValueAsString(draftDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isCreated())
            .andReturn();

        String draftResponseBody = draftResult.getResponse().getContentAsString();
        String originalRequestId = objectMapper.readTree(draftResponseBody).get("requestId").asText();

        System.out.println("Original Draft - RequestId: " + originalRequestId);
        System.out.println("Original Draft - Name: Haripriya");

        // Step 2: Submit draft with same data (Haripriya) using /register?requestId=...
        AccountRequestDTO submitDTO = createValidAccountRequest();

        MockMultipartFile requestPart = new MockMultipartFile(
            "request",
            "",
            "application/json",
            objectMapper.writeValueAsString(submitDTO).getBytes()
        );

        MvcResult submitResult = mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(requestPart)
                .param("requestId", originalRequestId)
                .contentType("multipart/form-data"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUBMITTED"))
            // CRITICAL: Verify the requestId remains the same
            .andExpect(jsonPath("$.requestId").value(originalRequestId))
            // Verify the data was updated to the submitted data (Haripriya), not different
            .andExpect(jsonPath("$.name").value("Haripriya"))
            .andExpect(jsonPath("$.dateOfBirth").value("15-05-1990"))
            .andExpect(jsonPath("$.address.streetName").value("Main Street"))
            .andExpect(jsonPath("$.address.houseNumber").value("123"))
            .andExpect(jsonPath("$.address.postCode").value("1234 AB"))
            .andExpect(jsonPath("$.address.city").value("Amsterdam"))
            .andExpect(jsonPath("$.accountType").value("SAVINGS"))
            .andReturn();

        String submitResponseBody = submitResult.getResponse().getContentAsString();
        String returnedRequestId = objectMapper.readTree(submitResponseBody).get("requestId").asText();
        String returnedName = objectMapper.readTree(submitResponseBody).get("name").asText();

        System.out.println("Submitted - RequestId: " + returnedRequestId);
        System.out.println("Submitted - Name: " + returnedName);

        // Additional assertion to ensure requestId didn't change
        assert originalRequestId.equals(returnedRequestId) :
            String.format("RequestId changed! Original: %s, Returned: %s", originalRequestId, returnedRequestId);

        // Verify the name is Haripriya
        assert "Haripriya".equals(returnedName) :
            String.format("Name not correct! Expected: Haripriya, Got: %s", returnedName);
    }

    @Test
    @DisplayName("Should handle validation errors with HTTP 400")
    void testRegister_InvalidRequest_Returns400() throws Exception {
        // Request with null name (violates @NotBlank)
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
        MockMultipartFile idDocument = createMockFile();

        mockMvc.perform(multipart("/api/v1/accounts/register")
                .file(idDocument)
                .part(new MockPart("request", objectMapper.writeValueAsString(invalidDTO).getBytes()))
                .contentType("multipart/form-data"))
            .andExpect(status().isBadRequest());
    }
}

