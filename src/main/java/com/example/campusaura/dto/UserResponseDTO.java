package com.example.campusaura.dto;

import com.google.cloud.Timestamp;

/**
 * DTO for user data returned by the admin user management API.
 * Maps from the real Firestore User entity fields.
 */
public class UserResponseDTO {

    private String uid;           // Firebase UID (document ID)
    private String name;          // Display name
    private String email;
    private String role;          // STUDENT, COORDINATOR, ADMIN, EXTERNAL_USER
    private boolean verified;     // Student ID verified
    private String degreeProgram; // For STUDENT role only
    private String studentIdUrl;  // URL to uploaded student ID image
    private Timestamp createdAt;

    // Constructors
    public UserResponseDTO() {}

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getDegreeProgram() { return degreeProgram; }
    public void setDegreeProgram(String degreeProgram) { this.degreeProgram = degreeProgram; }

    public String getStudentIdUrl() { return studentIdUrl; }
    public void setStudentIdUrl(String studentIdUrl) { this.studentIdUrl = studentIdUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
