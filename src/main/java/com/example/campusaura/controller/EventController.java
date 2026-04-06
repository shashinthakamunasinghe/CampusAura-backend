package com.example.campusaura.controller;

import com.example.campusaura.dto.EventDetailDTO;
import com.example.campusaura.dto.EventRequestDTO;
import com.example.campusaura.dto.EventResponseDTO;
import com.example.campusaura.dto.LandingPageEventDTO;
import com.example.campusaura.model.Event;
import com.example.campusaura.service.EventService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Create a new event
     * POST /api/events
     */
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody EventRequestDTO eventRequest) {
        try {
            // Extract coordinator ID from Firebase token
            String coordinatorId = extractUserIdFromToken(authHeader);

            // Create event
            Event createdEvent = eventService.createEvent(coordinatorId, eventRequest);

            // Return response
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create event: " + e.getMessage()));
        }
    }

    /**
     * Get event by ID (only own events)
     * GET /api/events/{eventId}
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String eventId) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            Event event = eventService.getEventById(eventId);
            
            if (event == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Event not found with ID: " + eventId));
            }
            
            // Check if the event belongs to the authenticated coordinator
            if (!event.getCoordinatorId().equals(coordinatorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("You don't have permission to view this event"));
            }
            
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve event: " + e.getMessage()));
        }
    }

    /**
     * Get all events (only coordinator's own events)
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            List<Event> events;

            // Filter by status (but only for the authenticated coordinator)
            if (status != null && !status.isEmpty()) {
                events = eventService.getEventsByCoordinator(coordinatorId);
                // Further filter by status
                events = events.stream()
                        .filter(event -> status.equals(event.getStatus()))
                        .toList();
            }
            // Get all events for the authenticated coordinator
            else {
                events = eventService.getEventsByCoordinator(coordinatorId);
            }

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Get events by coordinator (authenticated user)
     * GET /api/events/my-events
     */
    @GetMapping("/my-events")
    public ResponseEntity<?> getMyEvents(@RequestHeader("Authorization") String authHeader) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            List<Event> events = eventService.getEventsByCoordinator(coordinatorId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve your events: " + e.getMessage()));
        }
    }

    /**
     * Get random ongoing events for landing page (PUBLIC - no authentication required)
     * GET /api/events/landing-page
     * @param limit Optional query parameter to specify number of events (default: 10, max: 20)
     */
    @GetMapping("/landing-page")
    public ResponseEntity<?> getLandingPageEvents(@RequestParam(defaultValue = "10") int limit) {
        try {
            // Enforce maximum limit of 20 events
            int effectiveLimit = Math.min(limit, 20);
            List<LandingPageEventDTO> events = eventService.getRandomOngoingEvents(effectiveLimit);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve landing page events: " + e.getMessage()));
        }
    }

    /**
     * Get latest 3 events for landing page (PUBLIC - no authentication required)
     * GET /api/events/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestEvents() {
        try {
            // Get 3 latest published/ongoing events
            List<LandingPageEventDTO> events = eventService.getLatestEvents(3);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve latest events: " + e.getMessage()));
        }
    }

    /**
     * Get latest events for landing page with optional limit (PUBLIC - no authentication required)
     * GET /api/events/public/latest?limit=3
     */
    @GetMapping("/public/latest")
    public ResponseEntity<?> getPublicLatestEvents(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            // Get latest published/ongoing events with specified limit
            List<LandingPageEventDTO> events = eventService.getLatestEvents(limit);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve latest events: " + e.getMessage()));
        }
    }

    /**
     * Get all published events for public events page (PUBLIC - no authentication required)
     * GET /api/events/public?category=Technology&sortBy=upcoming
     * @param category - Filter by category (All, Technology, Career, Culture, Sports). Default: All
     * @param sortBy - Sort order (upcoming, latest, popular). Default: upcoming
     */
    @GetMapping("/public")
    public ResponseEntity<?> getPublicEvents(
            @RequestParam(required = false, defaultValue = "All") String category,
            @RequestParam(required = false, defaultValue = "upcoming") String sortBy) {
        try {
            // Get all published events with filtering and sorting
            List<LandingPageEventDTO> events = eventService.getPublicEvents(category, sortBy);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve events: " + e.getMessage()));
        }
    }

    /**
     * Get single event details for public event detail page (PUBLIC - no authentication required)
     * GET /api/events/public/{eventId}
     * @param eventId - The unique identifier of the event
     */
    @GetMapping("/public/{eventId}")
    public ResponseEntity<?> getPublicEventById(@PathVariable String eventId) {
        try {
            System.out.println("Fetching event details for eventId: " + eventId);
            // Get full event details
            EventDetailDTO eventDetail = eventService.getEventDetailById(eventId);
            System.out.println("Successfully fetched event: " + eventDetail.getTitle());
            return ResponseEntity.ok(eventDetail);
        } catch (RuntimeException e) {
            System.err.println("Event not found: " + eventId + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error fetching event: " + eventId + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve event details: " + e.getMessage()));
        }
    }

    /**
     * Update event
     * PUT /api/events/{eventId}
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String eventId,
            @RequestBody EventRequestDTO eventRequest) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            Event updatedEvent = eventService.updateEvent(eventId, coordinatorId, eventRequest);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update event: " + e.getMessage()));
        }
    }

    /**
     * Delete event
     * DELETE /api/events/{eventId}
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String eventId) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            eventService.deleteEvent(eventId, coordinatorId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event deleted successfully");
            response.put("eventId", eventId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete event: " + e.getMessage()));
        }
    }

    /**
     * Update event status
     * PATCH /api/events/{eventId}/status
     */
    @PatchMapping("/{eventId}/status")
    public ResponseEntity<?> updateEventStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String eventId,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String coordinatorId = extractUserIdFromToken(authHeader);
            String newStatus = statusUpdate.get("status");

            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Status is required"));
            }

            Event updatedEvent = eventService.updateEventStatus(eventId, coordinatorId, newStatus);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update event status: " + e.getMessage()));
        }
    }

    /**
     * Extract user ID from Firebase authentication token
     */
    private String extractUserIdFromToken(String authHeader) throws FirebaseAuthException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authHeader.substring(7);
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return decodedToken.getUid();
    }

    /**
     * Create error response map
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
