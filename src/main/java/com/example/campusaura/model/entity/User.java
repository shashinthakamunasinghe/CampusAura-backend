package com.example.campusaura.model.entity;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity stored in Firestore.
 * Represents application user data, separate from Firebase Authentication.
 *
 * Document path: users/{uid}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String uid;
    private String email;
    private String name;

    private String role;      // STUDENT, COORDINATOR, ADMIN, EXTERNAL_USER
    private boolean verified; // student ID verified or not

    // Student-specific fields
    private String degreeProgram;  // For STUDENT role only
    private String studentIdUrl;   // URL to uploaded student ID image (Firebase Storage)

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
