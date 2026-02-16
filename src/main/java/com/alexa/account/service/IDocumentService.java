package com.alexa.account.service;

import com.alexa.account.model.AccountRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for ID document management.
 * Defines contracts for document validation and processing.
 */
public interface IDocumentService {

    /**
     * Validate ID document file.
     * Checks if document is provided and has valid MIME type.
     *
     * @param idDocument the document file to validate
     * @throws com.alexa.account.exception.InvalidRequestException if document is invalid
     */
    void validateIdDocument(MultipartFile idDocument);

    /**
     * Process and set ID document to account request.
     * Converts MultipartFile to IdDocument entity and sets it on the AccountRequest.
     *
     * @param accountRequest the account request to update
     * @param idDocument the document file to process
     * @throws com.alexa.account.exception.InvalidRequestException if document cannot be processed
     */
    void processAndSetIdDocument(AccountRequest accountRequest, MultipartFile idDocument);

    /**
     * Validate and process ID document in a single operation.
     * Useful when both validation and processing are needed.
     *
     * @param accountRequest the account request to update
     * @param idDocument the document file to validate and process
     */
    void validateAndProcess(AccountRequest accountRequest, MultipartFile idDocument);
}

