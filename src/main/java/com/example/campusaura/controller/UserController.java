package com.example.campusaura.controller;

import com.example.campusaura.model.entity.User;
import com.example.campusaura.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User controller with role management and profile endpoints.
 * Uses simplified authentication with UID string (industry standard pattern).
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the authenticated user's profile.
     * @param uid Firebase UID from authentication token
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", user.getUid());
        profile.put("email", user.getEmail());
        profile.put("name", user.getName());
        profile.put("role", user.getRole());

        return ResponseEntity.ok(profile);
    }

    /**
     * Get full user details from Firestore.
     * Returns complete User entity including verification status.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);
        return ResponseEntity.ok(user);
    }

    /**
     * Admin dashboard - only accessible by ADMIN role.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard(
            @AuthenticationPrincipal String uid) {

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Admin Dashboard");
        dashboard.put("adminId", uid);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Update user role (ADMIN only).
     * Validates email domain requirements for protected roles.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{targetUid}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable String targetUid,
            @RequestBody Map<String, String> request) {

        String newRole = request.get("role");

        try {
            userService.updateUserRole(targetUid, newRole);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role updated successfully");
            response.put("uid", targetUid);
            response.put("newRole", newRole);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Student courses - only accessible by STUDENT role.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses")
    public ResponseEntity<Map<String, Object>> getStudentCourses(
            @AuthenticationPrincipal String uid) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student Courses");
        response.put("studentId", uid);

        return ResponseEntity.ok(response);
    }

    /**
     * Coordinator events - only accessible by COORDINATOR role.
     */
    @PreAuthorize("hasRole('COORDINATOR')")
    @GetMapping("/coordinator/events")
    public ResponseEntity<Map<String, Object>> getCoordinatorEvents(
            @AuthenticationPrincipal String uid) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Coordinator Events");
        response.put("coordinatorId", uid);

        return ResponseEntity.ok(response);
    }

    /**
     * Dashboard accessible by any authenticated user.
     * Shows different content based on role.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("welcome", "Welcome to CampusAura");
        dashboard.put("userId", uid);
        dashboard.put("role", user.getRole());

        return ResponseEntity.ok(dashboard);
    }
}
