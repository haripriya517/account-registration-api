package com.alexa.account.service;

import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.model.AccountRequest;
import com.alexa.account.model.IdDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of IDocumentService.
 * Handles ID document validation and processing.
 * Separates document-related concerns from AccountService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService implements IDocumentService {

    private final IFileStorageService fileStorageService;

    /**
     * Process and set ID document to account request.
     * Stores file to disk and saves metadata to the entity.
     */
    @Override
    public void processAndSetIdDocument(AccountRequest accountRequest, MultipartFile idDocument) {
        // Store file to disk and get the relative path
        String filePath = fileStorageService.storeFile(idDocument, "id-documents");

        // Set document metadata on account request
        accountRequest.setIdDocument(new IdDocument(
                filePath,
                idDocument.getOriginalFilename(),
                idDocument.getContentType(),
                idDocument.getSize()
        ));

        log.info("Document stored successfully for account request: {}", accountRequest.getRequestId());
    }

    /**
     * Validate ID document file.
     * Checks if document is provided and has valid MIME type.
     */
    @Override
    public void validateIdDocument(MultipartFile idDocument) {
        if (idDocument == null || idDocument.isEmpty()) {
            throw new InvalidRequestException("ID document is mandatory");
        }

        String contentType = idDocument.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new InvalidRequestException("ID document must be an image (JPG, PNG) or PDF");
        }
    }

    /**
     * Validate and process ID document in a single operation.
     * Useful when both validation and processing are needed.
     */
    @Override
    public void validateAndProcess(AccountRequest accountRequest, MultipartFile idDocument) {
        validateIdDocument(idDocument);
        processAndSetIdDocument(accountRequest, idDocument);
    }
}
