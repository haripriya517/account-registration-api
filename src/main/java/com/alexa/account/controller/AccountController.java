package com.alexa.account.controller;

import com.alexa.account.dto.*;
import com.alexa.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Account Controller - API Version 1
 * Handles account registration, draft operations, and retrieval.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Register a new account or submit existing draft.
     * - If requestId is provided: retrieves existing draft from database and submits it
     * - If requestId is NOT provided: creates new account registration
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccountResponseDTO> registerOrSubmit(
            @RequestParam(value = "requestId", required = false) String requestId,
            @RequestPart("request") @Valid AccountRequestDTO requestDTO,
            @RequestPart(value = "idDocument", required = false) MultipartFile idDocument) {

        AccountResponseDTO response = accountService.registerOrSubmit(requestId, requestDTO, idDocument);

        // Return 201 for new registration, 200 for draft submission
        if (requestId == null || requestId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Save as draft (registration, with Name, Address and Date of birth validation).
     */
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccountResponseDTO> saveDraft(
            @RequestPart("request") @Valid DraftRequestDTO requestDTO,
            @RequestPart(value = "idDocument", required = false) MultipartFile idDocument) {
        AccountResponseDTO response = accountService.saveDraft(requestDTO, idDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update existing draft with validation (resume registration).
     */
    @PutMapping(value = "/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccountResponseDTO> updateDraft(
            @PathVariable String requestId,
            @RequestPart("request") @Valid AccountRequestDTO requestDTO,
            @RequestPart(value = "idDocument", required = false) MultipartFile idDocument) {
        AccountResponseDTO response = accountService.updateDraft(requestId, requestDTO, idDocument);
        return ResponseEntity.ok(response);
    }

    /**
     * Get account request by request ID.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<AccountResponseDTO> getByRequestId(@PathVariable String requestId) {
        AccountResponseDTO response = accountService.getByRequestId(requestId);
        return ResponseEntity.ok(response);
    }
}
