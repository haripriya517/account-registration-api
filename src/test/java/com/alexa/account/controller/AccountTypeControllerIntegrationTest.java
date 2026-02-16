package com.alexa.account.controller;

import com.alexa.account.BaseIntegrationTest;
import com.alexa.account.model.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AccountTypeController Integration Tests")
class AccountTypeControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should get all account types with HTTP 200")
    void testGetAllAccountTypes_ReturnsAllTypes() throws Exception {
        mockMvc.perform(get("/api/v1/account-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[*]", containsInAnyOrder(
                AccountType.SAVINGS.toString(),
                AccountType.CURRENT.toString(),
                AccountType.INVESTMENT.toString()
            )));
    }

    @Test
    @DisplayName("Should get account types with JSON array response")
    void testGetAllAccountTypes_ReturnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/v1/account-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(Iterable.class)))
            .andExpect(jsonPath("$[0]", notNullValue()));
    }

    @Test
    @DisplayName("Should verify SAVINGS account type is available")
    void testGetAllAccountTypes_ContainsSavings() throws Exception {
        mockMvc.perform(get("/api/v1/account-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*]", hasItem(AccountType.SAVINGS.toString())));
    }

    @Test
    @DisplayName("Should verify CURRENT account type is available")
    void testGetAllAccountTypes_ContainsCurrent() throws Exception {
        mockMvc.perform(get("/api/v1/account-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*]", hasItem(AccountType.CURRENT.toString())));
    }

    @Test
    @DisplayName("Should verify INVESTMENT account type is available")
    void testGetAllAccountTypes_ContainsInvestment() throws Exception {
        mockMvc.perform(get("/api/v1/account-types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*]", hasItem(AccountType.INVESTMENT.toString())));
    }
}

