package com.example.campusaura.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    /**
     * Supports two formats:
     * - "classpath:firebase-service-account.json"  → local dev (from resources/)
     * - "/run/secrets/firebase-key"                → Docker secret file path
     */
    @Value("${firebase.service-account-key}")
    private String serviceAccountKeyPath;

    @Value("${firebase.database-url}")
    private String databaseUrl;

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @PostConstruct
    public void initialize() throws IOException {
        try (InputStream stream = openServiceAccountStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            } else {
                System.out.println("Firebase already initialized");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Opens the Firebase service account key from either:
     * 1. A classpath resource (local dev): "classpath:firebase-service-account.json"
     * 2. An absolute file path (Docker secret): "/run/secrets/firebase-key"
     */
    private InputStream openServiceAccountStream() throws IOException {
        if (serviceAccountKeyPath.startsWith("classpath:")) {
            String resourceName = serviceAccountKeyPath.substring("classpath:".length());
            InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (stream == null) {
                throw new IOException("Classpath resource not found: " + resourceName);
            }
            return stream;
        } else {
            // Absolute file path (Docker secret mounted at /run/secrets/)
            if (!Files.exists(Paths.get(serviceAccountKeyPath))) {
                throw new IOException("Firebase key file not found at: " + serviceAccountKeyPath);
            }
            return new FileInputStream(serviceAccountKeyPath);
        }
    }
}