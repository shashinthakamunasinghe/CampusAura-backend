package com.example.campusaura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for external user registration request from frontend.
 * Contains minimal fields needed for external user account creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserRegistrationRequest {

    private String name;
    private String email;  // Can be any valid email
}
