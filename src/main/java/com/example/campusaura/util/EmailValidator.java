package com.example.campusaura.util;

/**
 * Email validation utility for CampusAura.
 * Enforces university email domain rules.
 */
public final class EmailValidator {

    private static final String UNIVERSITY_DOMAIN = "@std.uwu.ac.lk";

    private EmailValidator() {
        // Prevent instantiation
    }

    /**
     * Checks if an email belongs to the university domain.
     *
     * @param email Email address to validate
     * @return true if email ends with @std.uwu.ac.lk, false otherwise
     */
    public static boolean isUniversityEmail(String email) {
        return email != null && email.toLowerCase().endsWith(UNIVERSITY_DOMAIN);
    }
}
