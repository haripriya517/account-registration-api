package com.alexa.account.service;

import com.alexa.account.BaseIntegrationTest;
import com.alexa.account.dto.AccountRequestDTO;
import com.alexa.account.dto.AccountResponseDTO;
import com.alexa.account.dto.AddressDTO;
import com.alexa.account.dto.DraftRequestDTO;
import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.exception.ResourceNotFoundException;
import com.alexa.account.model.AccountStatus;
import com.alexa.account.model.AccountType;
import com.alexa.account.repository.AccountRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountService Integration Tests")
class AccountServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRequestRepository accountRequestRepository;

    @Test
    @DisplayName("Should register account and persist to H2 database")
    void testRegister_ValidRequest_SavesToDatabaseAndReturnsResponse() {
        // Arrange
        AccountRequestDTO requestDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        // Act
        AccountResponseDTO response = accountService.registerOrSubmit(null, requestDTO, idDocument);

        // Assert
        assertNotNull(response);
        assertNotNull(response.requestId());
        assertEquals("Haripriya", response.name());
        assertEquals(LocalDate.of(1990, 5, 15), response.dateOfBirth());

        // Verify database persistence
        assertTrue(accountRequestRepository.findByRequestId(response.requestId()).isPresent());
    }

    @Test
    @DisplayName("Should save draft and persist to H2 database")
    void testSaveDraft_ValidRequest_SavesToDatabaseAndReturnsResponse() {
        // Arrange
        com.alexa.account.dto.DraftRequestDTO draftDTO = new com.alexa.account.dto.DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        // Act
        AccountResponseDTO response = accountService.saveDraft(draftDTO, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.requestId());
        assertEquals("Haripriya", response.name());

        // Verify database persistence
        assertTrue(accountRequestRepository.findByRequestId(response.requestId()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve account by request ID from H2 database")
    void testGetByRequestId_ExistingRequestId_ReturnsAccountResponse() {
        // Arrange - First save an account
        AccountRequestDTO requestDTO = new AccountRequestDTO(
            "Test User",
            LocalDate.of(1995, 3, 10),
            new AddressDTO("Test St", "999", "5678 XY", "Amsterdam"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "id.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        AccountResponseDTO savedResponse = accountService.registerOrSubmit(null, requestDTO, idDocument);
        String requestId = savedResponse.requestId();

        // Act
        AccountResponseDTO retrievedResponse = accountService.getByRequestId(requestId);

        // Assert
        assertNotNull(retrievedResponse);
        assertEquals("Test User", retrievedResponse.name());
        assertEquals(requestId, retrievedResponse.requestId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting non-existent request ID")
    void testGetByRequestId_NonExistentRequestId_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () ->
            accountService.getByRequestId("NON-EXISTENT-ID")
        );
    }

    @Test
    @DisplayName("Should register with all optional fields")
    void testRegister_WithAllOptionalFields_SavesSuccessfully() {
        AccountRequestDTO requestDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            BigDecimal.valueOf(1000),
            "haripriya@example.com",
            BigDecimal.valueOf(5000),
            true
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO response = accountService.registerOrSubmit(null, requestDTO, idDocument);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000).stripTrailingZeros(), response.startingBalance().stripTrailingZeros());
        assertEquals("haripriya@example.com", response.email());
        assertTrue(response.interestedInOtherProducts());
    }

    @Test
    @DisplayName("Should save draft with all optional fields")
    void testSaveDraft_WithAllOptionalFields_SavesSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.INVESTMENT,
            BigDecimal.valueOf(1000),
            "haripriya@example.com",
            BigDecimal.valueOf(5000),
            true
        );

        AccountResponseDTO response = accountService.saveDraft(draftDTO, null);

        assertNotNull(response);
        assertEquals("Haripriya", response.name());
        assertEquals(AccountType.INVESTMENT, response.accountType());
        assertEquals(AccountStatus.DRAFT, response.status());
        assertEquals(BigDecimal.valueOf(1000).stripTrailingZeros(), response.startingBalance().stripTrailingZeros());
    }

    @Test
    @DisplayName("Should save draft with ID document")
    void testSaveDraft_WithIdDocument_SavesSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO response = accountService.saveDraft(draftDTO, idDocument);

        assertNotNull(response);
        assertNotNull(response.requestId());
        assertTrue(accountRequestRepository.findByRequestId(response.requestId()).isPresent());
    }

    @Test
    @DisplayName("Should update draft successfully")
    void testUpdateDraft_ValidDraft_UpdatesSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, null);
        String requestId = savedDraft.requestId();

        AccountRequestDTO updateDTO = new AccountRequestDTO(
            "Haripriya Updated",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "46B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            BigDecimal.valueOf(5000),
            "haripriya.updated@example.com",
            BigDecimal.valueOf(6000),
            false
        );

        AccountResponseDTO updatedResponse = accountService.updateDraft(requestId, updateDTO, null);

        assertEquals("Haripriya Updated", updatedResponse.name());
        assertEquals("46B", updatedResponse.address().houseNumber());
        assertEquals(AccountType.SAVINGS, updatedResponse.accountType());
    }

    @Test
    @DisplayName("Should update draft with new ID document")
    void testUpdateDraft_WithNewIdDocument_UpdatesSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, null);
        String requestId = savedDraft.requestId();

        AccountRequestDTO updateDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MultipartFile newIdDocument = new MockMultipartFile(
            "file",
            "newpassport.jpg",
            "image/jpeg",
            "new content".getBytes()
        );

        AccountResponseDTO updatedResponse = accountService.updateDraft(requestId, updateDTO, newIdDocument);

        assertNotNull(updatedResponse);
        assertEquals(requestId, updatedResponse.requestId());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent draft")
    void testUpdateDraft_NonExistentDraft_ThrowsException() {
        AccountRequestDTO updateDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        assertThrows(ResourceNotFoundException.class, () ->
            accountService.updateDraft("NON-EXISTENT", updateDTO, null)
        );
    }

    @Test
    @DisplayName("Should throw exception when updating submitted request")
    void testUpdateDraft_SubmittedRequest_ThrowsException() {
        AccountRequestDTO requestDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO registered = accountService.registerOrSubmit(null, requestDTO, idDocument);
        String requestId = registered.requestId();

        AccountRequestDTO updateDTO = new AccountRequestDTO(
            "Haripriya Updated",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "124", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        assertThrows(InvalidRequestException.class, () ->
            accountService.updateDraft(requestId, updateDTO, null)
        );
    }

    @Test
    @DisplayName("Should submit draft successfully")
    void testSubmitDraft_ValidDraft_SubmitsSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertEquals(requestId, submittedResponse.requestId());
    }

    @Test
    @DisplayName("Should submit draft with new ID document")
    void testSubmitDraft_WithNewIdDocument_SubmitsSuccessfully() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, null);
        String requestId = savedDraft.requestId();

        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, idDocument);

        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
    }

    @Test
    @DisplayName("Should throw exception when submitting non-existent draft")
    void testSubmitDraft_NonExistentDraft_ThrowsException() {
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        assertThrows(ResourceNotFoundException.class, () ->
            accountService.submitDraft("NON-EXISTENT", submitDTO, null)
        );
    }

    @Test
    @DisplayName("Should throw exception when submitting already submitted request")
    void testSubmitDraft_AlreadySubmitted_ThrowsException() {
        AccountRequestDTO requestDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO registered = accountService.registerOrSubmit(null, requestDTO, idDocument);
        String requestId = registered.requestId();

        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1990, 5, 15),
            new AddressDTO("Main Street", "123", "1234 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        assertThrows(InvalidRequestException.class, () ->
            accountService.submitDraft(requestId, submitDTO, null)
        );
    }

    @Test
    @DisplayName("Should throw exception when submitting draft without ID document")
    void testSubmitDraft_WithoutIdDocument_ThrowsException() {
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, null);
        String requestId = savedDraft.requestId();

        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        assertThrows(InvalidRequestException.class, () ->
            accountService.submitDraft(requestId, submitDTO, null)
        );
    }

    @Test
    @DisplayName("Should submit draft with all optional fields")
    void testSubmitDraft_WithAllOptionalFields_SubmitsSuccessfully() {
        // Arrange - Save draft with minimal fields
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        // Act - Submit with all optional fields
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.INVESTMENT,
            BigDecimal.valueOf(10000),
            "haripriya@example.com",
            BigDecimal.valueOf(7500),
            true
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertEquals(AccountType.INVESTMENT, submittedResponse.accountType());
        assertEquals(BigDecimal.valueOf(10000).stripTrailingZeros(), submittedResponse.startingBalance().stripTrailingZeros());
        assertEquals("haripriya@example.com", submittedResponse.email());
        assertEquals(BigDecimal.valueOf(7500).stripTrailingZeros(), submittedResponse.monthlySalary().stripTrailingZeros());
        assertTrue(submittedResponse.interestedInOtherProducts());
    }

    @Test
    @DisplayName("Should submit draft and update all mandatory fields")
    void testSubmitDraft_UpdatesMandatoryFields_Successfully() {
        // Arrange - Save draft
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Original Name",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("OldStreet", "10", "1000 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        // Act - Submit with updated mandatory fields
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Updated Name",
            LocalDate.of(1990, 12, 25),
            new AddressDTO("NewStreet", "99", "2000 XY", "Rotterdam"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert
        assertEquals("Updated Name", submittedResponse.name());
        assertEquals(LocalDate.of(1990, 12, 25), submittedResponse.dateOfBirth());
        assertEquals("NewStreet", submittedResponse.address().streetName());
        assertEquals("99", submittedResponse.address().houseNumber());
        assertEquals("2000 XY", submittedResponse.address().postCode());
        assertEquals("Rotterdam", submittedResponse.address().city());
        assertEquals(AccountType.SAVINGS, submittedResponse.accountType());
    }

    @Test
    @DisplayName("Should submit draft with document replacement")
    void testSubmitDraft_ReplaceExistingDocument_SubmitsSuccessfully() {
        // Arrange - Save draft with initial document
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile oldDocument = new MockMultipartFile(
            "file",
            "old_passport.jpg",
            "image/jpeg",
            "old document content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, oldDocument);
        String requestId = savedDraft.requestId();

        // Act - Submit with new document
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Haripriya",
            LocalDate.of(1985, 8, 20),
            new AddressDTO("Keizersgracht", "45B", "1015 AB", "Amsterdam"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        MultipartFile newDocument = new MockMultipartFile(
            "file",
            "new_passport.pdf",
            "application/pdf",
            "new document content".getBytes()
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, newDocument);

        // Assert
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertNotNull(submittedResponse.idDocument());
        assertEquals("new_passport.pdf", submittedResponse.idDocument().documentName());
    }

    @Test
    @DisplayName("Should verify status changes from DRAFT to SUBMITTED")
    void testSubmitDraft_VerifyStatusChange_FromDraftToSubmitted() {
        // Arrange - Save draft and verify DRAFT status
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Status Test User",
            LocalDate.of(1992, 3, 15),
            new AddressDTO("StatusStreet", "55", "3000 CD", "Utrecht"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "status_test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        // Verify initial DRAFT status
        AccountResponseDTO draftResponse = accountService.getByRequestId(requestId);
        assertEquals(AccountStatus.DRAFT, draftResponse.status());

        // Act - Submit draft
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Status Test User",
            LocalDate.of(1992, 3, 15),
            new AddressDTO("StatusStreet", "55", "3000 CD", "Utrecht"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert - Verify status changed to SUBMITTED
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());

        // Verify persistence
        AccountResponseDTO retrievedResponse = accountService.getByRequestId(requestId);
        assertEquals(AccountStatus.SUBMITTED, retrievedResponse.status());
    }

    @Test
    @DisplayName("Should submit draft and reuse existing document")
    void testSubmitDraft_ReuseExistingDocument_SubmitsSuccessfully() {
        // Arrange - Save draft with document
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Reuse Doc User",
            LocalDate.of(1988, 7, 10),
            new AddressDTO("ReuseStreet", "77", "4000 EF", "Den Haag"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "existing_doc.jpg",
            "image/jpeg",
            "existing content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();
        String originalDocumentName = savedDraft.idDocument().documentName();

        // Act - Submit without providing new document (should reuse existing)
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Reuse Doc User",
            LocalDate.of(1988, 7, 10),
            new AddressDTO("ReuseStreet", "77", "4000 EF", "Den Haag"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert - Document should be the same as before
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertNotNull(submittedResponse.idDocument());
        assertEquals(originalDocumentName, submittedResponse.idDocument().documentName());
    }

    @Test
    @DisplayName("Should submit draft with empty file when document exists")
    void testSubmitDraft_EmptyFileWithExistingDoc_SubmitsSuccessfully() {
        // Arrange - Save draft with document
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Empty File Test",
            LocalDate.of(1991, 11, 5),
            new AddressDTO("EmptyStreet", "88", "5000 GH", "Eindhoven"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "valid_doc.jpg",
            "image/jpeg",
            "valid content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        // Act - Submit with empty multipart file (should reuse existing document)
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Empty File Test",
            LocalDate.of(1991, 11, 5),
            new AddressDTO("EmptyStreet", "88", "5000 GH", "Eindhoven"),
            AccountType.INVESTMENT,
            null,
            null,
            null,
            null
        );

        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, emptyFile);

        // Assert - Should succeed using existing document
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertNotNull(submittedResponse.idDocument());
    }

    @Test
    @DisplayName("Should submit draft and preserve optional null fields")
    void testSubmitDraft_PreserveNullOptionalFields_Successfully() {
        // Arrange - Save draft with some optional fields
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Null Fields User",
            LocalDate.of(1987, 4, 22),
            new AddressDTO("NullStreet", "33", "6000 IJ", "Groningen"),
            AccountType.SAVINGS,
            BigDecimal.valueOf(5000),
            null,  // email is null
            null,  // monthlySalary is null
            null   // interestedInOtherProducts is null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "null_test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();

        // Act - Submit with null optional fields
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Null Fields User",
            LocalDate.of(1987, 4, 22),
            new AddressDTO("NullStreet", "33", "6000 IJ", "Groningen"),
            AccountType.SAVINGS,
            BigDecimal.valueOf(5000),
            null,  // email remains null
            null,  // monthlySalary remains null
            null   // interestedInOtherProducts remains null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertEquals(BigDecimal.valueOf(5000).stripTrailingZeros(), submittedResponse.startingBalance().stripTrailingZeros());
        assertNull(submittedResponse.email());
        assertNull(submittedResponse.monthlySalary());
        assertNull(submittedResponse.interestedInOtherProducts());
    }

    @Test
    @DisplayName("Should submit draft with different account type than draft")
    void testSubmitDraft_ChangeAccountType_SubmitsSuccessfully() {
        // Arrange - Save draft with one account type
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Account Type Change",
            LocalDate.of(1993, 9, 18),
            new AddressDTO("TypeStreet", "44", "7000 KL", "Maastricht"),
            AccountType.CURRENT,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "type_change.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String requestId = savedDraft.requestId();
        assertEquals(AccountType.CURRENT, savedDraft.accountType());

        // Act - Submit with different account type
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Account Type Change",
            LocalDate.of(1993, 9, 18),
            new AddressDTO("TypeStreet", "44", "7000 KL", "Maastricht"),
            AccountType.INVESTMENT,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(requestId, submitDTO, null);

        // Assert
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
        assertEquals(AccountType.INVESTMENT, submittedResponse.accountType());
    }

    @Test
    @DisplayName("Should submit draft and verify request ID remains unchanged")
    void testSubmitDraft_RequestIdUnchanged_AfterSubmission() {
        // Arrange
        DraftRequestDTO draftDTO = new DraftRequestDTO(
            "Request ID Test",
            LocalDate.of(1989, 6, 30),
            new AddressDTO("IDStreet", "66", "8000 MN", "Zwolle"),
            null,
            null,
            null,
            null,
            null
        );

        MultipartFile idDocument = new MockMultipartFile(
            "file",
            "request_id_test.jpg",
            "image/jpeg",
            "test content".getBytes()
        );

        AccountResponseDTO savedDraft = accountService.saveDraft(draftDTO, idDocument);
        String originalRequestId = savedDraft.requestId();

        // Act
        AccountRequestDTO submitDTO = new AccountRequestDTO(
            "Request ID Test",
            LocalDate.of(1989, 6, 30),
            new AddressDTO("IDStreet", "66", "8000 MN", "Zwolle"),
            AccountType.SAVINGS,
            null,
            null,
            null,
            null
        );

        AccountResponseDTO submittedResponse = accountService.submitDraft(originalRequestId, submitDTO, null);

        // Assert - Request ID should remain the same
        assertEquals(originalRequestId, submittedResponse.requestId());
        assertEquals(AccountStatus.SUBMITTED, submittedResponse.status());
    }
}
