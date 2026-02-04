package com.example.campusaura.security;

import java.util.Map;

/**
 * Represents an authenticated Firebase user in the application.
 * This principal is stored in the Spring Security context and can be accessed
 * using @AuthenticationPrincipal in controllers.
 * Role is fetched from Firestore (single source of truth), not Firebase custom claims.
 */
public class FirebasePrincipal {

    private final String uid;
    private final String email;
    private final String name;
    private final String role;
    private final Map<String, Object> claims;

    public FirebasePrincipal(String uid, String email, String name, String role, Map<String, Object> claims) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.claims = claims;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    /**
     * Gets the role from Firestore (set during construction).
     * This is the authoritative role, not from Firebase custom claims.
     */
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "FirebasePrincipal{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
