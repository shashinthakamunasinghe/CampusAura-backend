package com.example.campusaura.service;

import com.example.campusaura.model.entity.User;

/**
 * Service interface for User business logic.
 * Manages sync between Firebase Auth and Firestore user data.
 */
public interface UserService {

    /**
     * Gets existing user or creates a new one if not exists.
     * Called during authentication to ensure user exists in Firestore.
     * Automatically assigns role based on email domain.
     *
     * @param uid Firebase UID
     * @param email User's email
     * @param name User's display name
     * @return User entity (existing or newly created)
     */
    User getOrCreateUser(String uid, String email, String name);

    /**
     * Gets user by Firebase UID.
     * Throws exception if user not found.
     *
     * @param uid Firebase UID
     * @return User entity
     * @throws RuntimeException if user not found
     */
    User getUserByUid(String uid);

    /**
     * Updates user role.
     * Validates that only university emails can have protected roles.
     *
     * @param uid Firebase UID
     * @param newRole New role to assign
     * @throws IllegalStateException if email domain doesn't match role requirements
     */
    void updateUserRole(String uid, String newRole);

    /**
     * Validates if a user can have a specific role based on email domain.
     * Protected roles (STUDENT, COORDINATOR, ADMIN) require university email.
     *
     * @param email User's email
     * @param role Role to validate
     * @return true if user can have this role, false otherwise
     */
    boolean canHaveRole(String email, String role);

    /**
     * Updates student profile with degree program and student ID.
     * Called after Firebase registration to add student-specific data.
     *
     * @param uid Firebase UID
     * @param degreeProgram Student's degree program
     * @param studentIdUrl URL to uploaded student ID image
     * @return Updated User entity
     */
    User updateStudentProfile(String uid, String degreeProgram, String studentIdUrl);
}
