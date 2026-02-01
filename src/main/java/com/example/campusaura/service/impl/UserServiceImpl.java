package com.example.campusaura.service.impl;

import com.example.campusaura.model.entity.User;
import com.example.campusaura.repository.UserRepository;
import com.example.campusaura.security.Roles;
import com.example.campusaura.service.UserService;
import com.example.campusaura.util.EmailValidator;
import com.google.cloud.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserService.
 * Handles user creation and synchronization with Firebase Auth.
 * Enforces email domain rules for role assignment.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getOrCreateUser(String uid, String email, String name) {
        logger.debug("Getting or creating user: {}", uid);

        return userRepository.findByUid(uid)
                .orElseGet(() -> {
                    logger.info("Creating new user in Firestore: {}", uid);

                    // Enforce email domain rule
                    boolean isUniversityEmail = EmailValidator.isUniversityEmail(email);

                    // University emails → STUDENT, Others → EXTERNAL_USER
                    String role = isUniversityEmail ? Roles.STUDENT : Roles.EXTERNAL_USER;

                    logger.info("Assigning role {} to user {} (university email: {})",
                            role, uid, isUniversityEmail);

                    User newUser = User.builder()
                            .uid(uid)
                            .email(email)
                            .name(name)
                            .role(role)          // Role based on email domain
                            .verified(false)     // not verified by default
                            .createdAt(Timestamp.now())
                            .build();

                    userRepository.save(newUser);
                    logger.info("New user created successfully: {} with role: {}", uid, role);

                    return newUser;
                });
    }

    @Override
    public User getUserByUid(String uid) {
        logger.debug("Fetching user by UID: {}", uid);

        return userRepository.findByUid(uid)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", uid);
                    return new RuntimeException("User not found: " + uid);
                });
    }

    @Override
    public void updateUserRole(String uid, String newRole) {
        logger.info("Attempting to update role for user: {} to {}", uid, newRole);

        User user = getUserByUid(uid);

        // Validate that user can have this role based on email domain
        if (!canHaveRole(user.getEmail(), newRole)) {
            logger.error("Cannot assign role {} to user {} with email {}",
                    newRole, uid, user.getEmail());
            throw new IllegalStateException(
                    "Only university emails (@std.uwu.ac.lk) can have STUDENT, COORDINATOR, or ADMIN roles"
            );
        }

        user.setRole(newRole);
        userRepository.save(user);

        logger.info("Successfully updated role for user {} to {}", uid, newRole);
    }

    @Override
    public boolean canHaveRole(String email, String role) {
        // EXTERNAL_USER can be anyone
        if (Roles.EXTERNAL_USER.equals(role)) {
            return true;
        }

        // Protected roles (STUDENT, COORDINATOR, ADMIN) require university email
        if (Roles.STUDENT.equals(role) ||
            Roles.COORDINATOR.equals(role) ||
            Roles.ADMIN.equals(role)) {
            return EmailValidator.isUniversityEmail(email);
        }

        // Unknown role
        return false;
    }

    @Override
    public User updateStudentProfile(String uid, String degreeProgram, String studentIdUrl) {
        logger.info("Updating student profile for user: {}", uid);

        User user = getUserByUid(uid);

        // Validate user is a student
        if (!Roles.STUDENT.equals(user.getRole())) {
            logger.error("Cannot update student profile for non-student user: {}", uid);
            throw new IllegalStateException("Only STUDENT role can have degree program and student ID");
        }

        // Update student-specific fields
        user.setDegreeProgram(degreeProgram);
        user.setStudentIdUrl(studentIdUrl);
        user.setUpdatedAt(Timestamp.now());

        userRepository.save(user);

        logger.info("Successfully updated student profile for user: {}", uid);
        return user;
    }
}
