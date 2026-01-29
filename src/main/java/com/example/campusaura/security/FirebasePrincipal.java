package com.example.campusaura.security;

import java.util.Map;

/**
 * Represents an authenticated Firebase user in the application.
 * This principal is stored in the Spring Security context and can be accessed
 * using @AuthenticationPrincipal in controllers.
 */
public class FirebasePrincipal {

    private final String uid;
    private final String email;
    private final String name;
    private final Map<String, Object> claims;

    public FirebasePrincipal(String uid, String email, String name, Map<String, Object> claims) {
        this.uid = uid;
        this.email = email;
        this.name = name;
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
     * Gets the role from Firebase custom claims.
     * Defaults to "USER" if no role is set.
     */
    public String getRole() {
        return claims.getOrDefault("role", "USER").toString().toUpperCase();
    }

    @Override
    public String toString() {
        return "FirebasePrincipal{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}
