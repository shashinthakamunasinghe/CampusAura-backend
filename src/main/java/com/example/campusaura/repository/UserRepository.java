package com.example.campusaura.repository;

import com.example.campusaura.model.entity.User;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Clean abstraction for Firestore access - testable and maintainable.
 */
public interface UserRepository {

    /**
     * Saves or updates a user in Firestore.
     * Uses UID as document ID (upsert operation).
     */
    void save(User user);

    /**
     * Finds a user by Firebase UID.
     * Returns Optional to avoid null handling.
     */
    Optional<User> findByUid(String uid);

    /**
     * Checks if a user exists by UID.
     * Useful for quick existence checks without fetching full data.
     */
    boolean existsByUid(String uid);
}
