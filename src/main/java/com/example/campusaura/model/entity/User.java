package com.example.campusaura.model.entity;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a user in the system.
 * Stored in Firestore 'users' collection with UID as document ID.
 * Supports both university students and external users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Firebase Authentication UID (unique identifier).
     * Used as Firestore document ID for 1:1 mapping.
     */
    private String uid;

    /**
     * User's email address.
     * University emails (@std.uwu.ac.lk) get special role privileges.
     */
    private String email;

    /**
     * User's display name.
     */
    private String name;

    /**
     * User role: STUDENT, COORDINATOR, ADMIN, or EXTERNAL_USER.
     * Automatically assigned based on email domain during registration.
     */
    private String role;

    /**
     * Verification status.
     * True if user has been verified by admin/coordinator.
     */
    private boolean verified;

    /**
     * Degree program (STUDENT only).
     * Populated during student registration.
     */
    private String degreeProgram;

    /**
     * URL to student ID image in Firebase Storage (STUDENT only).
     * Used for verification purposes.
     */
    private String studentIdUrl;

    /**
     * Timestamp when user was created.
     */
    private Timestamp createdAt;

    /**
     * Timestamp when user was last updated.
     */
    private Timestamp updatedAt;
}
