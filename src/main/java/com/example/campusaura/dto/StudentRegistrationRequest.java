package com.example.campusaura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for student registration request from frontend.
 * Contains all fields needed for student account creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationRequest {

    private String name;
    private String email;          // Must be @std.uwu.ac.lk
    private String degreeProgram;
    private String studentIdUrl;   // URL to uploaded student ID (from Firebase Storage)
}
