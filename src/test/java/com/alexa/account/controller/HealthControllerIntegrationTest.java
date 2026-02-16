package com.alexa.account.controller;

import com.alexa.account.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("HealthController Integration Tests")
class HealthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return health status UP with HTTP 200")
    void testGetHealth_ReturnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Should return valid JSON response")
    void testGetHealth_ReturnsValidJson() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @DisplayName("Should return health endpoint responds in reasonable time")
    void testGetHealth_ReturnsQuickly() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Health check should respond in less than 1 second
        assert duration < 1000 : "Health check took too long: " + duration + "ms";
    }

    @Test
    @DisplayName("Should be idempotent - multiple calls return same status")
    void testGetHealth_Idempotent() throws Exception {
        // First call
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));

        // Second call
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}

