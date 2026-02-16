package com.alexa.account.service;

import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.model.AccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@DisplayName("DocumentService Tests")
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private IFileStorageService fileStorageService;

    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = new DocumentService(fileStorageService);
        // Mock file storage to return a path (lenient to avoid unnecessary stubbing errors)
        lenient().when(fileStorageService.storeFile(any(MultipartFile.class), eq("id-documents")))
                .thenReturn("id-documents/test-file.jpg");
    }

    @Test
    @DisplayName("Should validate valid image document (JPEG)")
    void testValidateIdDocument_ValidJpeg_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should validate valid image document (PNG)")
    void testValidateIdDocument_ValidPng_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.png",
            "image/png",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should validate valid PDF document")
    void testValidateIdDocument_ValidPdf_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should reject empty document")
    void testValidateIdDocument_Empty_ThrowsException() {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should reject null document")
    void testValidateIdDocument_Null_ThrowsException() {
        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(null));
    }

    @Test
    @DisplayName("Should reject invalid file type (text)")
    void testValidateIdDocument_InvalidType_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "content".getBytes()
        );

        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should process and set document successfully")
    void testProcessAndSetIdDocument_ValidFile_SetsDocument() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "content".getBytes()
        );

        AccountRequest accountRequest = new AccountRequest();
        service.processAndSetIdDocument(accountRequest, file);

        assertNotNull(accountRequest.getIdDocument());
        assertEquals("passport.jpg", accountRequest.getIdDocument().getFileName());
        assertEquals("image/jpeg", accountRequest.getIdDocument().getFileType());
    }

    @Test
    @DisplayName("Should validate and process document in single call")
    void testValidateAndProcess_ValidFile_Success() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "content".getBytes()
        );

        AccountRequest accountRequest = new AccountRequest();
        assertDoesNotThrow(() -> service.validateAndProcess(accountRequest, file));
        assertNotNull(accountRequest.getIdDocument());
    }

    @Test
    @DisplayName("Should reject invalid file type in validateAndProcess")
    void testValidateAndProcess_InvalidFile_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "content".getBytes()
        );

        AccountRequest accountRequest = new AccountRequest();
        assertThrows(InvalidRequestException.class, () -> service.validateAndProcess(accountRequest, file));
    }

    @Test
    @DisplayName("Should reject document with null content type")
    void testValidateIdDocument_NullContentType_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.jpg",
            null,
            "content".getBytes()
        );

        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should handle various image types - PNG")
    void testValidateIdDocument_PngVariant_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.png",
            "image/png",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should handle various image types - GIF")
    void testValidateIdDocument_GifVariant_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.gif",
            "image/gif",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should handle various image types - WebP")
    void testValidateIdDocument_WebpVariant_NoException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.webp",
            "image/webp",
            "content".getBytes()
        );

        assertDoesNotThrow(() -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should reject Word document")
    void testValidateIdDocument_WordDoc_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "content".getBytes()
        );

        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should reject Excel spreadsheet")
    void testValidateIdDocument_ExcelDoc_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "content".getBytes()
        );

        assertThrows(InvalidRequestException.class, () -> service.validateIdDocument(file));
    }

    @Test
    @DisplayName("Should process and set document with correct properties")
    void testProcessAndSetIdDocument_CheckAllProperties_Success() {
        byte[] testContent = "test passport content".getBytes();
        MultipartFile file = new MockMultipartFile(
            "file",
            "my-passport.jpg",
            "image/jpeg",
            testContent
        );

        AccountRequest accountRequest = new AccountRequest();
        service.processAndSetIdDocument(accountRequest, file);

        assertNotNull(accountRequest.getIdDocument());
        assertEquals("my-passport.jpg", accountRequest.getIdDocument().getFileName());
        assertEquals("image/jpeg", accountRequest.getIdDocument().getFileType());
        assertEquals("id-documents/test-file.jpg", accountRequest.getIdDocument().getFilePath());
        assertEquals(testContent.length, accountRequest.getIdDocument().getFileSize());
    }

    @Test
    @DisplayName("Should validate and process PDF document")
    void testValidateAndProcess_PdfDocument_Success() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "id-card.pdf",
            "application/pdf",
            "pdf content".getBytes()
        );

        AccountRequest accountRequest = new AccountRequest();
        assertDoesNotThrow(() -> service.validateAndProcess(accountRequest, file));
        assertNotNull(accountRequest.getIdDocument());
        assertEquals("id-card.pdf", accountRequest.getIdDocument().getFileName());
        assertEquals("application/pdf", accountRequest.getIdDocument().getFileType());
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when file has no content type")
    void testValidateAndProcess_NoContentType_ThrowsException() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.jpg",
            null,
            "content".getBytes()
        );

        AccountRequest accountRequest = new AccountRequest();
        assertThrows(InvalidRequestException.class, () -> service.validateAndProcess(accountRequest, file));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException when file is empty in validateAndProcess")
    void testValidateAndProcess_EmptyFile_ThrowsException() {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        AccountRequest accountRequest = new AccountRequest();
        assertThrows(InvalidRequestException.class, () -> service.validateAndProcess(accountRequest, file));
    }
}

