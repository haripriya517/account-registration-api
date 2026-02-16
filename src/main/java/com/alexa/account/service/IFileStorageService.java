package com.alexa.account.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for file storage operations.
 * Handles storing, loading, and deleting files from the file system.
 */
public interface IFileStorageService {

    /**
     * Store a file to the file system.
     * @param file the multipart file to store
     * @param category the category/subdirectory (e.g., "id-documents")
     * @return the relative path where the file was stored
     */
    String storeFile(MultipartFile file, String category);

    /**
     * Load a file as a Resource.
     * @param filename the relative path of the file
     * @return the file as a Resource
     */
    Resource loadFileAsResource(String filename);

    /**
     * Delete a file from storage.
     * @param filename the relative path of the file to delete
     */
    void deleteFile(String filename);

    /**
     * Check if a file exists.
     * @param filename the relative path of the file
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String filename);
}

