package com.alexa.account.controller;

import com.alexa.account.model.AccountType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Account Type Controller - API Version 1
 * Provides endpoints to retrieve available account types from enum.
 */
@RestController
@RequestMapping("/api/v1/account-types")
public class AccountTypeController {

    /**
     * Get all available account types from AccountType enum.
     *
     * @return List of all available account types
     */
    @GetMapping
    public ResponseEntity<List<AccountType>> getAllAccountTypes() {
        List<AccountType> accountTypes = Arrays.asList(AccountType.values());
        return ResponseEntity.ok(accountTypes);
    }
}
