package com.example.campusaura.security;

/**
 * Role constants for CampusAura.
 * Explicit constants prevent typos and improve maintainability.
 */
public final class Roles {

    public static final String ADMIN = "ADMIN";
    public static final String STUDENT = "STUDENT";
    public static final String COORDINATOR = "COORDINATOR";
    public static final String EXTERNAL_USER = "EXTERNAL_USER";

    private Roles() {
        // Prevent instantiation
    }
}
