package com.alexa.account.service;

import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileStorageService Tests")
class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @AfterEach
    void cleanup() throws IOException {
        // Clean up all files in temp directory after each test
        if (Files.exists(tempDir)) {
            try (Stream<Path> paths = Files.walk(tempDir)) {
                paths.sorted(Comparator.reverseOrder()) // Delete from deepest to shallowest
                        .forEach(path -> {
                            try {
                                if (!path.equals(tempDir)) {
                                    Files.deleteIfExists(path);
                                }
                            } catch (IOException e) {
                                // Ignore cleanup errors
                            }
                        });
            }
        }
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create upload directory on initialization")
    void testConstructor_CreatesUploadDirectory() {
        assertTrue(Files.exists(tempDir));
        assertTrue(Files.isDirectory(tempDir));
    }

    @Test
    @DisplayName("Should handle existing directory on initialization")
    void testConstructor_HandlesExistingDirectory() {
        // Directory already exists from setUp
        assertDoesNotThrow(() -> new FileStorageService(tempDir.toString()));
        assertTrue(Files.exists(tempDir));
    }

    // ==================== storeFile Tests ====================

    @Test
    @DisplayName("Should store file successfully")
    void testStoreFile_ValidFile_StoresSuccessfully() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        // Act
        String filePath = fileStorageService.storeFile(file, "id-documents");

        // Assert
        assertNotNull(filePath);
        assertTrue(filePath.startsWith("id-documents/"));
        assertTrue(filePath.endsWith(".jpg"));
        assertTrue(fileStorageService.fileExists(filePath));
    }

    @Test
    @DisplayName("Should create category subdirectory when storing file")
    void testStoreFile_CreatesSubdirectory() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "passport.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        // Act
        fileStorageService.storeFile(file, "id-documents");

        // Assert
        Path categoryPath = tempDir.resolve("id-documents");
        assertTrue(Files.exists(categoryPath));
        assertTrue(Files.isDirectory(categoryPath));
    }

    @Test
    @DisplayName("Should generate unique filename for each file")
    void testStoreFile_GeneratesUniqueFilenames() {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test.jpg",
                "image/jpeg",
                "content1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test.jpg",
                "image/jpeg",
                "content2".getBytes()
        );

        // Act
        String filePath1 = fileStorageService.storeFile(file1, "id-documents");
        String filePath2 = fileStorageService.storeFile(file2, "id-documents");

        // Assert
        assertNotEquals(filePath1, filePath2);
        assertTrue(fileStorageService.fileExists(filePath1));
        assertTrue(fileStorageService.fileExists(filePath2));
    }

    @Test
    @DisplayName("Should preserve file extension")
    void testStoreFile_PreservesExtension() {
        // Arrange
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );
        MockMultipartFile jpgFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        // Act
        String pdfPath = fileStorageService.storeFile(pdfFile, "documents");
        String jpgPath = fileStorageService.storeFile(jpgFile, "images");

        // Assert
        assertTrue(pdfPath.endsWith(".pdf"));
        assertTrue(jpgPath.endsWith(".jpg"));
    }

    @Test
    @DisplayName("Should handle file without extension")
    void testStoreFile_NoExtension_UsesBinExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "noextension",
                "application/octet-stream",
                "binary content".getBytes()
        );

        // Act
        String filePath = fileStorageService.storeFile(file, "documents");

        // Assert
        assertTrue(filePath.endsWith(".bin"));
    }

    @Test
    @DisplayName("Should throw exception when storing null file")
    void testStoreFile_NullFile_ThrowsException() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> fileStorageService.storeFile(null, "id-documents")
        );
        assertEquals("Cannot store empty file", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when storing empty file")
    void testStoreFile_EmptyFile_ThrowsException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> fileStorageService.storeFile(emptyFile, "documents")
        );
        assertEquals("Cannot store empty file", exception.getMessage());
    }

    @Test
    @DisplayName("Should replace existing file when storing with same path")
    void testStoreFile_ReplacesExistingFile() {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "original content".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "updated content".getBytes()
        );

        // Act
        String path1 = fileStorageService.storeFile(file1, "documents");
        String path2 = fileStorageService.storeFile(file2, "documents");

        // Assert - Both should succeed and create different files
        assertNotEquals(path1, path2);
        assertTrue(fileStorageService.fileExists(path1));
        assertTrue(fileStorageService.fileExists(path2));
    }

    @Test
    @DisplayName("Should store large file successfully")
    void testStoreFile_LargeFile_Succeeds() {
        // Arrange - Create 1MB file
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-file.bin",
                "application/octet-stream",
                largeContent
        );

        // Act
        String filePath = fileStorageService.storeFile(largeFile, "uploads");

        // Assert
        assertNotNull(filePath);
        assertTrue(fileStorageService.fileExists(filePath));
    }

    // ==================== loadFileAsResource Tests ====================

    @Test
    @DisplayName("Should load file as resource successfully")
    void testLoadFileAsResource_ExistingFile_ReturnsResource() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );
        String filePath = fileStorageService.storeFile(file, "documents");

        // Act
        Resource resource = fileStorageService.loadFileAsResource(filePath);

        // Assert
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("test content", new String(resource.getInputStream().readAllBytes()));
    }

    @Test
    @DisplayName("Should throw exception when loading non-existent file")
    void testLoadFileAsResource_NonExistentFile_ThrowsException() {
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> fileStorageService.loadFileAsResource("documents/nonexistent.txt")
        );
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    @DisplayName("Should prevent path traversal attack in loadFileAsResource")
    void testLoadFileAsResource_PathTraversal_ThrowsException() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> fileStorageService.loadFileAsResource("../../../etc/passwd")
        );
        assertEquals("Invalid file path detected", exception.getMessage());
    }

    @Test
    @DisplayName("Should prevent path traversal with encoded characters")
    void testLoadFileAsResource_EncodedPathTraversal_ThrowsException() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> fileStorageService.loadFileAsResource("documents/../../sensitive.txt")
        );
        assertEquals("Invalid file path detected", exception.getMessage());
    }

    // ==================== deleteFile Tests ====================

    @Test
    @DisplayName("Should delete file successfully")
    void testDeleteFile_ExistingFile_DeletesSuccessfully() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "todelete.txt",
                "text/plain",
                "content".getBytes()
        );
        String filePath = fileStorageService.storeFile(file, "documents");
        assertTrue(fileStorageService.fileExists(filePath));

        // Act
        fileStorageService.deleteFile(filePath);

        // Assert
        assertFalse(fileStorageService.fileExists(filePath));
    }

    @Test
    @DisplayName("Should handle deleting non-existent file gracefully")
    void testDeleteFile_NonExistentFile_NoException() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> fileStorageService.deleteFile("documents/nonexistent.txt"));
    }

    @Test
    @DisplayName("Should prevent path traversal in deleteFile")
    void testDeleteFile_PathTraversal_Ignored() throws IOException {
        // Arrange - Create a file outside upload directory (simulated)
        Path outsideFile = tempDir.getParent().resolve("outside.txt");
        Files.writeString(outsideFile, "sensitive");
        assertTrue(Files.exists(outsideFile));

        // Act - Try to delete file outside upload directory
        fileStorageService.deleteFile("../../outside.txt");

        // Assert - File should still exist (deletion should be prevented)
        assertTrue(Files.exists(outsideFile));

        // Cleanup
        Files.deleteIfExists(outsideFile);
    }

    @Test
    @DisplayName("Should delete file in subdirectory")
    void testDeleteFile_Subdirectory_DeletesSuccessfully() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nested.txt",
                "text/plain",
                "content".getBytes()
        );
        String filePath = fileStorageService.storeFile(file, "docs/nested/deep");

        // Act
        fileStorageService.deleteFile(filePath);

        // Assert
        assertFalse(fileStorageService.fileExists(filePath));
    }

    // ==================== fileExists Tests ====================

    @Test
    @DisplayName("Should return true for existing file")
    void testFileExists_ExistingFile_ReturnsTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "exists.txt",
                "text/plain",
                "content".getBytes()
        );
        String filePath = fileStorageService.storeFile(file, "documents");

        // Act & Assert
        assertTrue(fileStorageService.fileExists(filePath));
    }

    @Test
    @DisplayName("Should return false for non-existent file")
    void testFileExists_NonExistentFile_ReturnsFalse() {
        // Act & Assert
        assertFalse(fileStorageService.fileExists("documents/nonexistent.txt"));
    }

    @Test
    @DisplayName("Should return false for null filename")
    void testFileExists_NullFilename_ReturnsFalse() {
        // Act & Assert
        assertFalse(fileStorageService.fileExists(null));
    }

    @Test
    @DisplayName("Should return false for blank filename")
    void testFileExists_BlankFilename_ReturnsFalse() {
        // Act & Assert
        assertFalse(fileStorageService.fileExists(""));
        assertFalse(fileStorageService.fileExists("   "));
    }

    @Test
    @DisplayName("Should handle file existence check in subdirectory")
    void testFileExists_Subdirectory_Works() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nested.txt",
                "text/plain",
                "content".getBytes()
        );
        String filePath = fileStorageService.storeFile(file, "level1/level2/level3");

        // Act & Assert
        assertTrue(fileStorageService.fileExists(filePath));
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Should handle complete file lifecycle")
    void testFileLifecycle_StoreLoadDelete() throws IOException {
        // Arrange
        String content = "Test content for lifecycle";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lifecycle.txt",
                "text/plain",
                content.getBytes()
        );

        // Act & Assert - Store
        String filePath = fileStorageService.storeFile(file, "lifecycle-test");
        assertNotNull(filePath);
        assertTrue(fileStorageService.fileExists(filePath));

        // Act & Assert - Load
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        assertNotNull(resource);
        assertEquals(content, new String(resource.getInputStream().readAllBytes()));

        // Act & Assert - Delete
        fileStorageService.deleteFile(filePath);
        assertFalse(fileStorageService.fileExists(filePath));
    }

    @Test
    @DisplayName("Should handle multiple files in different categories")
    void testMultipleCategories_StoresIndependently() {
        // Arrange
        MockMultipartFile doc1 = new MockMultipartFile(
                "file1",
                "doc1.pdf",
                "application/pdf",
                "doc1".getBytes()
        );
        MockMultipartFile doc2 = new MockMultipartFile(
                "file2",
                "doc2.pdf",
                "application/pdf",
                "doc2".getBytes()
        );
        MockMultipartFile img1 = new MockMultipartFile(
                "file3",
                "img1.jpg",
                "image/jpeg",
                "img1".getBytes()
        );

        // Act
        String docPath1 = fileStorageService.storeFile(doc1, "documents");
        String docPath2 = fileStorageService.storeFile(doc2, "documents");
        String imgPath1 = fileStorageService.storeFile(img1, "images");

        // Assert
        assertTrue(docPath1.startsWith("documents/"));
        assertTrue(docPath2.startsWith("documents/"));
        assertTrue(imgPath1.startsWith("images/"));
        assertTrue(fileStorageService.fileExists(docPath1));
        assertTrue(fileStorageService.fileExists(docPath2));
        assertTrue(fileStorageService.fileExists(imgPath1));
    }

    @Test
    @DisplayName("Should handle special characters in original filename")
    void testStoreFile_SpecialCharactersInFilename() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test file (with) special & chars!.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        // Act
        String filePath = fileStorageService.storeFile(file, "documents");

        // Assert
        assertNotNull(filePath);
        assertTrue(filePath.endsWith(".jpg"));
        assertTrue(fileStorageService.fileExists(filePath));
    }

    @Test
    @DisplayName("Should handle uppercase file extensions")
    void testStoreFile_UppercaseExtension_ConvertsToLowercase() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.PDF",
                "application/pdf",
                "content".getBytes()
        );

        // Act
        String filePath = fileStorageService.storeFile(file, "documents");

        // Assert
        assertTrue(filePath.endsWith(".pdf")); // Should be lowercase
    }

    @Test
    @DisplayName("Should store binary files correctly")
    void testStoreFile_BinaryFile_StoresCorrectly() throws IOException {
        // Arrange
        byte[] binaryContent = {0x00, 0x01, 0x02, (byte) 0xFF, (byte) 0xFE};
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "binary.dat",
                "application/octet-stream",
                binaryContent
        );

        // Act
        String filePath = fileStorageService.storeFile(file, "binary");

        // Assert
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        byte[] loadedContent = resource.getInputStream().readAllBytes();
        assertArrayEquals(binaryContent, loadedContent);
    }

    @Test
    @DisplayName("Should handle concurrent file storage")
    void testStoreFile_Concurrent_HandlesCorrectly() throws InterruptedException {
        // Arrange
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        String[] filePaths = new String[threadCount];

        // Act - Create multiple threads storing files simultaneously
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                MockMultipartFile file = new MockMultipartFile(
                        "file" + index,
                        "concurrent" + index + ".txt",
                        "text/plain",
                        ("content" + index).getBytes()
                );
                filePaths[index] = fileStorageService.storeFile(file, "concurrent");
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - All files should be stored with unique paths
        for (int i = 0; i < threadCount; i++) {
            assertNotNull(filePaths[i]);
            assertTrue(fileStorageService.fileExists(filePaths[i]));
        }

        // All paths should be unique
        for (int i = 0; i < threadCount; i++) {
            for (int j = i + 1; j < threadCount; j++) {
                assertNotEquals(filePaths[i], filePaths[j]);
            }
        }
    }
}



