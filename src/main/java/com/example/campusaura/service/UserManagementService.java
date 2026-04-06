package com.example.campusaura.service;

import com.example.campusaura.dto.UserResponseDTO;
import com.example.campusaura.dto.UserStatsDTO;
import com.example.campusaura.model.User;
import com.example.campusaura.security.Roles;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for admin user management operations.
 * All operations work against the real User entity stored in Firestore.
 * User document ID = Firebase UID.
 */
@Service
public class UserManagementService {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "users";

    // ─────────────────────────────────────────────────────────────────────────
    // User Retrieval
    // ─────────────────────────────────────────────────────────────────────────

    /** Get all users */
    public List<UserResponseDTO> getAllUsers() throws ExecutionException, InterruptedException {
        return fetchByQuery(firestore.collection(COLLECTION_NAME));
    }

    /**
     * Get users filtered by role (maps old "university-students" → STUDENT, "external-users" → EXTERNAL_USER).
     * @param role one of Roles.STUDENT, Roles.EXTERNAL_USER, Roles.COORDINATOR, Roles.ADMIN
     */
    public List<UserResponseDTO> getUsersByRole(String role) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("role", role);
        return fetchByQuery(query);
    }

    /** Get users whose student ID is not yet verified (verified = false AND role = STUDENT) */
    public List<UserResponseDTO> getPendingVerificationUsers() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("role", Roles.STUDENT)
                .whereEqualTo("verified", false);
        return fetchByQuery(query);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Statistics
    // ─────────────────────────────────────────────────────────────────────────

    public UserStatsDTO getUserStats() throws ExecutionException, InterruptedException {
        List<User> allUsers = getAllUsersInternal();

        long students  = allUsers.stream().filter(u -> Roles.STUDENT.equals(u.getRole())).count();
        long externals = allUsers.stream().filter(u -> Roles.EXTERNAL_USER.equals(u.getRole())).count();
        long pending   = allUsers.stream()
                .filter(u -> Roles.STUDENT.equals(u.getRole()) && !u.isVerified())
                .count();

        return new UserStatsDTO(students, externals, pending);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mutations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verify (or un-verify) a student's ID.
     * @param uid      Firebase UID (document ID)
     * @param verified true = verified, false = rejected / unverified
     */
    public UserResponseDTO verifyStudent(String uid, boolean verified)
            throws ExecutionException, InterruptedException {

        if (uid == null || uid.isBlank()) throw new IllegalArgumentException("uid must not be null or blank");
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(uid);
        assertExists(docRef, uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("verified", verified);
        updates.put("updatedAt", Timestamp.now());
        docRef.update(updates).get();

        return documentToDTO(docRef.get().get());
    }

    /** Delete user document */
    public void deleteUser(String uid) throws ExecutionException, InterruptedException {
        if (uid == null || uid.isBlank()) throw new IllegalArgumentException("uid must not be null or blank");
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(uid);
        assertExists(docRef, uid);
        docRef.delete().get();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<UserResponseDTO> fetchByQuery(Query query)
            throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = query.get();
        return future.get().getDocuments().stream()
                .map(this::documentToDTO)
                .collect(Collectors.toList());
    }

    private List<User> getAllUsersInternal() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        return future.get().getDocuments().stream()
                .map(doc -> doc.toObject(User.class))
                .collect(Collectors.toList());
    }

    private UserResponseDTO documentToDTO(DocumentSnapshot doc) {
        User user = doc.toObject(User.class);
        if (user == null) return new UserResponseDTO();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUid(user.getUid());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setVerified(user.isVerified());
        dto.setDegreeProgram(user.getDegreeProgram());
        dto.setStudentIdUrl(user.getStudentIdUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    private void assertExists(DocumentReference docRef, String uid)
            throws ExecutionException, InterruptedException {
        if (!docRef.get().get().exists()) {
            throw new RuntimeException("User not found with uid: " + uid);
        }
    }
}
