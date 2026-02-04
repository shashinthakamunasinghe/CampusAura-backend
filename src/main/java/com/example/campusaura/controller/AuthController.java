package com.example.campusaura.controller;

import com.example.campusaura.dto.ExternalUserRegistrationRequest;
import com.example.campusaura.dto.StudentRegistrationRequest;
import com.example.campusaura.model.entity.User;
import com.example.campusaura.security.Roles;
import com.example.campusaura.service.UserService;
import com.example.campusaura.util.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller for CampusAura.
 *
 * IMPORTANT: Firebase handles actual authentication (register/login/logout) on the client side.
 * This controller provides:
 * - Email validation
 * - Registration metadata
 * - Current session info
 * - Logout confirmation
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Validate email and check what role it would receive.
     * PUBLIC endpoint - no authentication required.
     * Used by frontend before registration to inform user.
     */
    @PostMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createError("Email is required"));
        }

        boolean isUniversityEmail = EmailValidator.isUniversityEmail(email);
        String assignedRole = isUniversityEmail ? Roles.STUDENT : Roles.EXTERNAL_USER;

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("isUniversityEmail", isUniversityEmail);
        response.put("assignedRole", assignedRole);
        response.put("canSellItems", !Roles.EXTERNAL_USER.equals(assignedRole));
        response.put("message", isUniversityEmail
            ? "Valid university email - will be registered as STUDENT"
            : "External email - will be registered as EXTERNAL_USER with limited privileges");

        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user session info.
     * Called after Firebase login to confirm backend authentication.
     */
    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSession(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> session = new HashMap<>();
        session.put("authenticated", true);
        session.put("uid", user.getUid());
        session.put("email", user.getEmail());
        session.put("name", user.getName());
        session.put("role", user.getRole());
        session.put("isUniversityEmail", EmailValidator.isUniversityEmail(user.getEmail()));
        session.put("canSellItems", !Roles.EXTERNAL_USER.equals(user.getRole()));

        return ResponseEntity.ok(session);
    }

    /**
     * Verify token is valid (health check for authentication).
     * Returns minimal info to confirm authentication works.
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @AuthenticationPrincipal String uid) {

        User user = userService.getUserByUid(uid);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("uid", uid);
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint (backend confirmation).
     * Note: Actual logout happens on frontend via firebase.auth().signOut()
     * This endpoint just confirms the backend received the request.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @AuthenticationPrincipal String uid) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("uid", uid);
        response.put("note", "Firebase token should be cleared on client side");

        return ResponseEntity.ok(response);
    }

    /**
     * Get registration info for different roles.
     * PUBLIC endpoint - helps users understand registration requirements.
     */
    @GetMapping("/registration-info")
    public ResponseEntity<Map<String, Object>> getRegistrationInfo() {
        Map<String, Object> response = new HashMap<>();

        response.put("universityEmailDomain", "@std.uwu.ac.lk");

        Map<String, String> roleInfo = new HashMap<>();
        roleInfo.put(Roles.STUDENT, "University email required (@std.uwu.ac.lk)");
        roleInfo.put(Roles.COORDINATOR, "University email required - promoted by admin");
        roleInfo.put(Roles.ADMIN, "University email required - promoted by admin");
        roleInfo.put(Roles.EXTERNAL_USER, "Any valid email - limited privileges (cannot sell items)");

        response.put("roles", roleInfo);
        response.put("note", "Role is automatically assigned based on email domain during registration");

        return ResponseEntity.ok(response);
    }

    /**
     * Complete student registration with additional profile data.
     * Called after Firebase registration to CREATE user and save degree program and student ID.
     * Requires authentication (user must be logged in via Firebase).
     *
     * ✅ CORRECT PATTERN:
     * - Firebase Auth creates identity (done on frontend)
     * - This endpoint creates user in YOUR database
     * - Then updates student-specific fields
     */
    @PostMapping("/register/student/complete")
    public ResponseEntity<Map<String, Object>> completeStudentRegistration(
            @RequestAttribute("firebaseUid") String firebaseUid,
            @RequestAttribute("firebaseEmail") String firebaseEmail,
            @RequestAttribute("firebaseName") String firebaseName,
            @RequestBody StudentRegistrationRequest request) {

        try {
            // ✅ STEP 1: CREATE user in database if not exists
            User user = userService.getOrCreateUser(
                firebaseUid,
                firebaseEmail != null ? firebaseEmail : request.getEmail(),
                firebaseName != null ? firebaseName : request.getName()
            );

            // Validate this is a student account
            if (!Roles.STUDENT.equals(user.getRole())) {
                return ResponseEntity.badRequest()
                        .body(createError("Only STUDENT accounts can complete student registration"));
            }

            // Validate university email
            if (!EmailValidator.isUniversityEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(createError("Student registration requires university email (@std.uwu.ac.lk)"));
            }

            // ✅ STEP 2: Update student-specific profile data
            User updatedUser = userService.updateStudentProfile(
                    firebaseUid,
                    request.getDegreeProgram(),
                    request.getStudentIdUrl()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Student registration completed successfully");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(createError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createError("Failed to complete student registration: " + e.getMessage()));
        }
    }

    /**
     * Complete external user registration.
     * Called after Firebase registration to CREATE user in database.
     * Requires authentication (user must be logged in via Firebase).
     *
     * ✅ CORRECT PATTERN:
     * - Firebase Auth creates identity (done on frontend)
     * - This endpoint creates user in YOUR database
     * - Firebase Auth ≠ Your Database
     */
    @PostMapping("/register/external/complete")
    public ResponseEntity<Map<String, Object>> completeExternalUserRegistration(
            @RequestAttribute("firebaseUid") String firebaseUid,
            @RequestAttribute(value = "firebaseEmail", required = false) String firebaseEmail,
            @RequestAttribute(value = "firebaseName", required = false) String firebaseName,
            @RequestBody ExternalUserRegistrationRequest request) {

        try {
            // ✅ CREATE user in database if not exists
            // This is CRITICAL - Firebase does NOT create database users for you
            // Use request body data if Firebase attributes are null
            String email = firebaseEmail != null ? firebaseEmail : request.getEmail();
            String name = firebaseName != null ? firebaseName : request.getName();

            User user = userService.getOrCreateUser(firebaseUid, email, name);

            // Validate this is an external user account
            if (!Roles.EXTERNAL_USER.equals(user.getRole())) {
                return ResponseEntity.badRequest()
                        .body(createError("This endpoint is for external user registration only"));
            }


            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "External user registration completed successfully");
            response.put("user", user);
            response.put("note", "External users have limited privileges (cannot sell items)");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createError("Failed to complete external user registration: " + e.getMessage()));
        }
    }

    /**
     * Validate registration data before Firebase account creation.
     * Checks email format, domain, and provides expected role.
     * PUBLIC endpoint - no authentication required.
     */
    @PostMapping("/validate-registration")
    public ResponseEntity<Map<String, Object>> validateRegistration(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String userType = request.get("userType"); // "student" or "external"

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createError("Email is required"));
        }

        if (userType == null || userType.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createError("User type is required"));
        }

        boolean isUniversityEmail = EmailValidator.isUniversityEmail(email);
        Map<String, Object> response = new HashMap<>();

        // Validate student registration
        if ("student".equalsIgnoreCase(userType)) {
            if (!isUniversityEmail) {
                response.put("valid", false);
                response.put("error", "Student registration requires university email (@std.uwu.ac.lk)");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("valid", true);
            response.put("email", email);
            response.put("userType", "student");
            response.put("assignedRole", Roles.STUDENT);
            response.put("canSellItems", true);
            response.put("requiresDegreeProgram", true);
            response.put("requiresStudentId", true);
            response.put("message", "Valid student registration - university email confirmed");
        }
        // Validate external user registration
        else if ("external".equalsIgnoreCase(userType)) {
            if (isUniversityEmail) {
                response.put("valid", false);
                response.put("error", "University email should register as Student, not External User");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("valid", true);
            response.put("email", email);
            response.put("userType", "external");
            response.put("assignedRole", Roles.EXTERNAL_USER);
            response.put("canSellItems", false);
            response.put("requiresDegreeProgram", false);
            response.put("requiresStudentId", false);
            response.put("message", "Valid external user registration");
            response.put("note", "External users have limited privileges (cannot sell items)");
        } else {
            return ResponseEntity.badRequest().body(createError("Invalid user type. Must be 'student' or 'external'"));
        }

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createError(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
