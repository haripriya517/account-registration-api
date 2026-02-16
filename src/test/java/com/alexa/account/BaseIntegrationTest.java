package com.alexa.account;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base test class for Spring Boot integration tests.
 * Uses H2 in-memory database for testing.
 *
 * Features:
 * - Auto-configured MockMvc for testing REST endpoints
 * - H2 in-memory database for data persistence tests
 * - Automatic test database cleanup after each test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Base class for all integration tests
}

