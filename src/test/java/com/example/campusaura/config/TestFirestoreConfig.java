package com.example.campusaura.config;

import com.google.cloud.firestore.Firestore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Test configuration that provides a mock Firestore bean
 * This allows tests to run without Firebase credentials
 */
@TestConfiguration
public class TestFirestoreConfig {

    @Bean
    @Primary
    public Firestore firestore() {
        // Return a Mockito mock of Firestore for testing
        return mock(Firestore.class);
    }
}
