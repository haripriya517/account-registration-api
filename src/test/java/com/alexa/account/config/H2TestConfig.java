package com.alexa.account.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * Test configuration for H2 in-memory database.
 * Used for unit and integration tests.
 */
@TestConfiguration
public class H2TestConfig {

    /**
     * Configure H2 in-memory datasource for testing.
     * Database is created fresh for each test and destroyed after.
     *
     * @return DataSource configured for H2
     */
    @Bean
    public DataSource testDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;MODE=MySQL")
                .username("sa")
                .password("")
                .build();
    }
}

