package com.alexa.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable document metadata.
 * Stores file path instead of binary content for better performance.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdDocument {

    /**
     * Relative path to the stored file (e.g., "id-documents/20260215-120000-abc123.jpg")
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * Original filename from upload
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * MIME type of the file (e.g., "image/jpeg", "application/pdf")
     */
    @Column(name = "file_type")
    private String fileType;

    /**
     * File size in bytes
     */
    @Column(name = "file_size")
    private Long fileSize;
}
