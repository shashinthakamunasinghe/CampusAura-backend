package com.example.campusaura.controller;

import com.example.campusaura.dto.FeedbackDTO;
import com.example.campusaura.model.User;
import com.example.campusaura.service.FeedbackService;
import com.example.campusaura.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    /**
     * Get all feedback for an event (PUBLIC - no auth required)
     * GET /api/events/public/{eventId}/feedback
     */
    @GetMapping("/public/{eventId}/feedback")
    public ResponseEntity<?> getEventFeedback(@PathVariable String eventId) {
        try {
            List<FeedbackDTO> feedback = feedbackService.getFeedbackByEventId(eventId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Add feedback for an event (AUTH required)
     * POST /api/events/{eventId}/feedback
     */
    @PostMapping("/{eventId}/feedback")
    public ResponseEntity<?> addFeedback(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String eventId,
            @RequestBody Map<String, String> body) {
        try {
            String uid = extractUserIdFromToken(authHeader);
            String text = body.get("text");

            if (text == null || text.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Feedback text is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Get user name from Firestore
            User user = userService.getUserByUid(uid);
            String userName = user.getName() != null ? user.getName() : user.getEmail().split("@")[0];

            FeedbackDTO feedback = feedbackService.addFeedback(eventId, uid, userName, text.trim());
            return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to post feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private String extractUserIdFromToken(String authHeader) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return decodedToken.getUid();
    }
}
