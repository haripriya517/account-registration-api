package com.alexa.account.service;

import com.alexa.account.dto.*;
import com.alexa.account.exception.InvalidRequestException;
import com.alexa.account.exception.ResourceNotFoundException;
import com.alexa.account.mapper.AccountMapper;
import com.alexa.account.model.AccountRequest;
import com.alexa.account.model.AccountStatus;
import com.alexa.account.repository.AccountRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRequestRepository accountRequestRepository;
    private final AccountMapper accountMapper;
    private final IDocumentService documentService;
    private final RequestIdGeneratorService requestIdGeneratorService;

    /**
     * Register a new account OR submit existing draft.
     * - If requestId is provided: retrieves existing draft from database and submits it
     * - If requestId is NOT provided: creates new account registration
     */
    @Transactional
    @Override
    public AccountResponseDTO registerOrSubmit(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument) {
        // Check if requestId is provided
        if (requestId != null && !requestId.trim().isEmpty()) {
            // Submit existing draft
            return submitExistingDraft(requestId, requestDTO, idDocument);
        } else {
            // Create new registration
            return createNewRegistration(requestDTO, idDocument);
        }
    }

    /**
     * Create new registration (internal method).
     */
    private AccountResponseDTO createNewRegistration(AccountRequestDTO requestDTO, MultipartFile idDocument) {
        documentService.validateIdDocument(idDocument);

        AccountRequest accountRequest = new AccountRequest();
        populateMandatoryFields(accountRequest, requestDTO);
        populateOptionalFields(accountRequest, requestDTO);
        accountRequest.setStatus(AccountStatus.SUBMITTED);
        documentService.processAndSetIdDocument(accountRequest, idDocument);
        String generatedRequestId = requestIdGeneratorService.generateRequestId(accountRequest.getDateOfBirth());
        accountRequest.setRequestId(generatedRequestId);
        AccountRequest saved = accountRequestRepository.save(accountRequest);

        return accountMapper.accountRequestToResponseDTO(saved);
    }

    /**
     * Submit existing draft (internal method).
     */
    private AccountResponseDTO submitExistingDraft(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument) {
        AccountRequest accountRequest = accountRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found with id: " + requestId));

        if (accountRequest.getStatus() == AccountStatus.SUBMITTED) {
            throw new InvalidRequestException("Request has already been submitted");
        }

        // Check if document exists (either previously uploaded or new upload)
        boolean hasExistingDocument = accountRequest.getIdDocument() != null
                && accountRequest.getIdDocument().getFilePath() != null;
        boolean hasNewDocument = idDocument != null && !idDocument.isEmpty();

        // ID document is mandatory for submission - either existing or new
        if (!hasExistingDocument && !hasNewDocument) {
            throw new InvalidRequestException("ID document is mandatory for submission");
        }

        updateAccountFields(accountRequest, requestDTO);
        accountRequest.setStatus(AccountStatus.SUBMITTED);

        // Only process new document if provided, otherwise keep existing document
        if (hasNewDocument) {
            documentService.validateAndProcess(accountRequest, idDocument);
        }

        AccountRequest saved = accountRequestRepository.save(accountRequest);
        return accountMapper.accountRequestToResponseDTO(saved);
    }

    /**
     * Save a draft with minimum fields(Name, Address and Date of birth) validation (allows pausing the registration process).
     * ID document is optional for drafts.
     */
    @Transactional
    @Override
    public AccountResponseDTO saveDraft(DraftRequestDTO requestDTO, MultipartFile idDocument) {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setName(requestDTO.name());
        accountRequest.setDateOfBirth(requestDTO.dateOfBirth());
        accountRequest.setAddress(accountMapper.addressDtoToAddress(requestDTO.address()));

        // optional fields for drafts
        if (requestDTO.accountType() != null) {
            accountRequest.setAccountType(requestDTO.accountType());
        }
        populateOptionalFields(accountRequest, requestDTO);
        accountRequest.setStatus(AccountStatus.DRAFT);

        // Set ID document if provided (optional for draft)
        if (idDocument != null && !idDocument.isEmpty()) {
            documentService.processAndSetIdDocument(accountRequest, idDocument);
        }

        String requestId = requestIdGeneratorService.generateRequestId(accountRequest.getDateOfBirth());
        accountRequest.setRequestId(requestId);
        AccountRequest saved = accountRequestRepository.save(accountRequest);

        return accountMapper.accountRequestToResponseDTO(saved);
    }

    /**
     * Update an existing draft with minimum fields(Name, Address and Date of birth)validation.
     */
    @Transactional
    @Override
    public AccountResponseDTO updateDraft(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument) {
        AccountRequest accountRequest = accountRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found with id: " + requestId));

        if (accountRequest.getStatus() == AccountStatus.SUBMITTED) {
            throw new InvalidRequestException("Cannot update a submitted request");
        }

        updateAccountFields(accountRequest, requestDTO);

        if (idDocument != null && !idDocument.isEmpty()) {
            documentService.validateAndProcess(accountRequest, idDocument);
        }

        AccountRequest saved = accountRequestRepository.save(accountRequest);
        return accountMapper.accountRequestToResponseDTO(saved);
    }

    /**
     * Submit a draft with full validation.
     * Reuses document from draft if already uploaded, or accepts new document.
     */
    @Transactional
    @Override
    public AccountResponseDTO submitDraft(String requestId, AccountRequestDTO requestDTO, MultipartFile idDocument) {
        AccountRequest accountRequest = accountRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found with id: " + requestId));

        if (accountRequest.getStatus() == AccountStatus.SUBMITTED) {
            throw new InvalidRequestException("Request has already been submitted");
        }

        // Check if document exists (either previously uploaded or new upload)
        boolean hasExistingDocument = accountRequest.getIdDocument() != null
                && accountRequest.getIdDocument().getFilePath() != null;
        boolean hasNewDocument = idDocument != null && !idDocument.isEmpty();

        // ID document is mandatory for submission - either existing or new
        if (!hasExistingDocument && !hasNewDocument) {
            throw new InvalidRequestException("ID document is mandatory for submission");
        }

        updateAccountFields(accountRequest, requestDTO);
        accountRequest.setStatus(AccountStatus.SUBMITTED);

        // Only process new document if provided, otherwise keep existing document
        if (hasNewDocument) {
            documentService.validateAndProcess(accountRequest, idDocument);
        }

        AccountRequest saved = accountRequestRepository.save(accountRequest);
        return accountMapper.accountRequestToResponseDTO(saved);
    }

    /**
     * Get account request by request ID.
     */
    @Override
    public AccountResponseDTO getByRequestId(String requestId) {
        AccountRequest accountRequest = accountRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found with id: " + requestId));

        return accountMapper.accountRequestToResponseDTO(accountRequest);
    }

    /**
     * Populate mandatory fields for new account creation.
     */
    private void populateMandatoryFields(AccountRequest accountRequest, AccountRequestDTO requestDTO) {
        accountRequest.setName(requestDTO.name());
        accountRequest.setDateOfBirth(requestDTO.dateOfBirth());
        accountRequest.setAddress(accountMapper.addressDtoToAddress(requestDTO.address()));
        accountRequest.setAccountType(requestDTO.accountType());
    }

    /**
     * Populate optional fields (startingBalance, email, monthlySalary, interestedInOtherProducts).
     * Can be used with AccountRequestDTO or DraftRequestDTO (both have the same optional fields).
     */
    private void populateOptionalFields(AccountRequest accountRequest, Object requestDTO) {
        if (requestDTO instanceof AccountRequestDTO dto) {
            setOptionalField(accountRequest::setStartingBalance, dto.startingBalance());
            setOptionalField(accountRequest::setEmail, dto.email());
            setOptionalField(accountRequest::setMonthlySalary, dto.monthlySalary());
            setOptionalField(accountRequest::setInterestedInOtherProducts, dto.interestedInOtherProducts());
        } else if (requestDTO instanceof DraftRequestDTO dto) {
            setOptionalField(accountRequest::setStartingBalance, dto.startingBalance());
            setOptionalField(accountRequest::setEmail, dto.email());
            setOptionalField(accountRequest::setMonthlySalary, dto.monthlySalary());
            setOptionalField(accountRequest::setInterestedInOtherProducts, dto.interestedInOtherProducts());
        }
    }

    /**
     * Set optional field if value is not null.
     */
    private <T> void setOptionalField(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Update all account fields from DTO (for updating existing entities).
     */
    private void updateAccountFields(AccountRequest accountRequest, AccountRequestDTO requestDTO) {
        accountRequest.setName(requestDTO.name());
        accountRequest.setDateOfBirth(requestDTO.dateOfBirth());
        accountRequest.setAddress(accountMapper.addressDtoToAddress(requestDTO.address()));
        accountRequest.setAccountType(requestDTO.accountType());
        populateOptionalFields(accountRequest, requestDTO);
    }
}
