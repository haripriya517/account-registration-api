package com.alexa.account.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdDocument Model Tests")
class IdDocumentTest {

    @Test
    @DisplayName("Should create IdDocument with all constructor parameters")
    void testIdDocumentConstructor_AllFields_Success() {
        IdDocument doc = new IdDocument("id-documents/test.jpg", "passport.jpg", "image/jpeg", 1024L);

        assertEquals("id-documents/test.jpg", doc.getFilePath());
        assertEquals("passport.jpg", doc.getFileName());
        assertEquals("image/jpeg", doc.getFileType());
        assertEquals(1024L, doc.getFileSize());
    }

    @Test
    @DisplayName("Should create IdDocument with no-arg constructor")
    void testIdDocumentNoArgConstructor_Success() {
        IdDocument doc = new IdDocument();
        assertNotNull(doc);
    }

    @Test
    @DisplayName("Should set and get file path")
    void testSetGetFilePath_Success() {
        IdDocument doc = new IdDocument();
        doc.setFilePath("id-documents/test.jpg");
        assertEquals("id-documents/test.jpg", doc.getFilePath());
    }

    @Test
    @DisplayName("Should set and get file name")
    void testSetGetFileName_Success() {
        IdDocument doc = new IdDocument();
        doc.setFileName("passport.jpg");
        assertEquals("passport.jpg", doc.getFileName());
    }

    @Test
    @DisplayName("Should set and get file type")
    void testSetGetFileType_Success() {
        IdDocument doc = new IdDocument();
        doc.setFileType("image/jpeg");
        assertEquals("image/jpeg", doc.getFileType());
    }

    @Test
    @DisplayName("Should set and get file size")
    void testSetGetFileSize_Success() {
        IdDocument doc = new IdDocument();
        doc.setFileSize(2048L);
        assertEquals(2048L, doc.getFileSize());
    }

    @Test
    @DisplayName("Should handle multiple set operations")
    void testMultipleSetOperations_Success() {
        IdDocument doc = new IdDocument();

        doc.setFilePath("id-documents/test.pdf");
        doc.setFileName("id_card.pdf");
        doc.setFileType("application/pdf");
        doc.setFileSize(3072L);

        assertEquals("id-documents/test.pdf", doc.getFilePath());
        assertEquals("id_card.pdf", doc.getFileName());
        assertEquals("application/pdf", doc.getFileType());
        assertEquals(3072L, doc.getFileSize());
    }

    @Test
    @DisplayName("Should allow null values in fields")
    void testNullValues_Success() {
        IdDocument doc = new IdDocument(null, null, null, null);

        assertNull(doc.getFilePath());
        assertNull(doc.getFileName());
        assertNull(doc.getFileType());
        assertNull(doc.getFileSize());
    }

    @Test
    @DisplayName("Should preserve file metadata")
    void testFileMetadataIntegrity_Success() {
        IdDocument doc = new IdDocument("id-documents/file.bin", "file.bin", "application/octet-stream", 4096L);

        assertEquals("id-documents/file.bin", doc.getFilePath());
        assertEquals("file.bin", doc.getFileName());
        assertEquals("application/octet-stream", doc.getFileType());
        assertEquals(4096L, doc.getFileSize());
    }
}


