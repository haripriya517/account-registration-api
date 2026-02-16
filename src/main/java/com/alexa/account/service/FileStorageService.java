package com.alexa.account.service;

import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of file storage service.
 * Stores files to the file system instead of database.
 */
@Service
@Slf4j
public class FileStorageService implements IFileStorageService {

    private final Path uploadLocation;

    public FileStorageService(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadLocation);
            log.info("Upload directory created/verified at: {}", this.uploadLocation);
        } catch (IOException e) {
            throw new InvalidRequestException("Could not create upload directory: " + e.getMessage());
        }
    }

    @Override
    public String storeFile(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Cannot store empty file");
        }

        // Generate secure filename
        String filename = generateSecureFilename(file.getOriginalFilename());

        // Create category subdirectory
        Path categoryPath = this.uploadLocation.resolve(category);
        try {
            Files.createDirectories(categoryPath);
        } catch (IOException e) {
            throw new InvalidRequestException("Could not create category directory: " + e.getMessage());
        }

        Path targetLocation = categoryPath.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            // Stream file directly to disk - no memory loading
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", targetLocation);
            return category + "/" + filename;
        } catch (IOException e) {
            throw new InvalidRequestException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.uploadLocation.resolve(filename).normalize();

            // Security check: prevent path traversal attacks
            if (!filePath.startsWith(this.uploadLocation)) {
                throw new InvalidRequestException("Invalid file path detected");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + filename);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = this.uploadLocation.resolve(filename).normalize();

            // Security check: prevent path traversal attacks
            if (!filePath.startsWith(this.uploadLocation)) {
                log.warn("Attempted to delete file outside upload directory: {}", filename);
                return;
            }

            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filename, e);
        }
    }

    @Override
    public boolean fileExists(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }
        Path filePath = this.uploadLocation.resolve(filename).normalize();
        return Files.exists(filePath);
    }

    /**
     * Generate a secure filename to prevent collisions and security issues.
     * Format: YYYYMMDD-HHmmss-UUID.extension
     */
    private String generateSecureFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s-%s.%s", timestamp, uuid, extension);
    }

    /**
     * Extract file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "bin";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "bin";
    }
}

