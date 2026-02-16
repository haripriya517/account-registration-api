package com.alexa.account.service;

import com.alexa.account.dto.AccountRequestDTO;
import com.alexa.account.dto.AccountResponseDTO;
import com.alexa.account.dto.DraftRequestDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for account management operations.
 * Defines contracts for account registration, draft management, and retrieval.
 */
public interface IAccountService {

    /**
     * Register a new account OR submit existing draft.
     * - If requestId is provided: retrieves existing draft from database and submits it
     * - If requestId is NOT provided: creates new account registration
     *
     * @param requestId optional request ID (null or empty for new registration)
     * @param requestDTO the account request data
     * @param idDocument the ID document file (mandatory for new, optional for draft if already uploaded)
     * @return AccountResponseDTO with account details
     */
    AccountResponseDTO registerOrSubmit(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument);

    /**
     * Save registration as draft (pause registration).
     * Only name, dateOfBirth, and address are mandatory.
     *
     * @param requestDTO the draft request data
     * @param idDocument the ID document file (optional)
     * @return AccountResponseDTO with draft details
     */
    AccountResponseDTO saveDraft(DraftRequestDTO requestDTO, MultipartFile idDocument);

    /**
     * Update an existing draft with validation.
     * Resume and update draft with new information.
     *
     * @param requestId the unique request ID
     * @param requestDTO the updated account request data
     * @param idDocument the ID document file (optional)
     * @return AccountResponseDTO with updated draft details
     */
    AccountResponseDTO updateDraft(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument);

    /**
     * Submit a draft with full validation.
     * Complete the registration process.
     *
     * @param requestId the unique request ID
     * @param requestDTO the account request data for submission
     * @param idDocument the ID document file (optional if already uploaded)
     * @return AccountResponseDTO with submitted account details
     */
    AccountResponseDTO submitDraft(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument);

    /**
     * Get account request details by request ID.
     *
     * @param requestId the unique request ID
     * @return AccountResponseDTO with account details
     */
    AccountResponseDTO getByRequestId(String requestId);
}
