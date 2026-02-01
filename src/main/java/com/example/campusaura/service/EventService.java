package com.example.campusaura.service;

import com.example.campusaura.dto.EventRequestDTO;
import com.example.campusaura.model.Event;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final String COLLECTION_NAME = "events";

    @Autowired
    private Firestore firestore;

    /**
     * Create a new event
     */
    public Event createEvent(String coordinatorId, EventRequestDTO eventRequest) throws ExecutionException, InterruptedException {
        // Generate unique event ID
        String eventId = UUID.randomUUID().toString();
        String timestamp = Instant.now().toString();

        // Create Event object
        Event event = new Event();
        event.setEventId(eventId);
        event.setCoordinatorId(coordinatorId);
        event.setTitle(eventRequest.getTitle());
        event.setVenue(eventRequest.getVenue());
        event.setDateTime(eventRequest.getDateTime());
        event.setTicketsAvailable(eventRequest.getTicketsAvailable());
        event.setTicketCategories(eventRequest.getTicketCategories());
        event.setPastEventDetails(eventRequest.getPastEventDetails());
        event.setEventImageUrls(eventRequest.getEventImageUrls());
        event.setSellItems(eventRequest.getSellItems());
        event.setDescription(eventRequest.getDescription());
        event.setOrganizingDepartment(eventRequest.getOrganizingDepartment());
        event.setStatus(eventRequest.getStatus() != null ? eventRequest.getStatus() : "DRAFT");
        event.setCreatedAt(timestamp);
        event.setUpdatedAt(timestamp);

        // Convert to Map for Firestore
        Map<String, Object> eventData = convertEventToMap(event);

        // Save to Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(eventId)
                .set(eventData);
        result.get(); // Wait for completion

        return event;
    }

    /**
     * Get event by ID
     */
    public Event getEventById(String eventId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(eventId)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        return convertMapToEvent(document.getId(), document.getData());
    }

    /**
     * Get all events
     */
    public List<Event> getAllEvents() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Get all events by coordinator ID
     */
    public List<Event> getEventsByCoordinator(String coordinatorId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("coordinatorId", coordinatorId);

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Get events by status
     */
    public List<Event> getEventsByStatus(String status) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", status);

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Get events by department
     */
    public List<Event> getEventsByDepartment(String department) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("organizingDepartment", department);

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Update event
     */
    public Event updateEvent(String eventId, String coordinatorId, EventRequestDTO eventRequest) 
            throws ExecutionException, InterruptedException {
        // Check if event exists and belongs to coordinator
        Event existingEvent = getEventById(eventId);
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }

        if (!existingEvent.getCoordinatorId().equals(coordinatorId)) {
            throw new SecurityException("You don't have permission to update this event");
        }

        // Update event fields
        existingEvent.setTitle(eventRequest.getTitle());
        existingEvent.setVenue(eventRequest.getVenue());
        existingEvent.setDateTime(eventRequest.getDateTime());
        existingEvent.setTicketsAvailable(eventRequest.getTicketsAvailable());
        existingEvent.setTicketCategories(eventRequest.getTicketCategories());
        existingEvent.setPastEventDetails(eventRequest.getPastEventDetails());
        existingEvent.setEventImageUrls(eventRequest.getEventImageUrls());
        existingEvent.setSellItems(eventRequest.getSellItems());
        existingEvent.setDescription(eventRequest.getDescription());
        existingEvent.setOrganizingDepartment(eventRequest.getOrganizingDepartment());
        if (eventRequest.getStatus() != null) {
            existingEvent.setStatus(eventRequest.getStatus());
        }
        existingEvent.setUpdatedAt(Instant.now().toString());

        // Convert to Map for Firestore
        Map<String, Object> eventData = convertEventToMap(existingEvent);

        // Update in Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(eventId)
                .set(eventData);
        result.get(); // Wait for completion

        return existingEvent;
    }

    /**
     * Delete event
     */
    public boolean deleteEvent(String eventId, String coordinatorId) throws ExecutionException, InterruptedException {
        // Check if event exists and belongs to coordinator
        Event existingEvent = getEventById(eventId);
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }

        if (!existingEvent.getCoordinatorId().equals(coordinatorId)) {
            throw new SecurityException("You don't have permission to delete this event");
        }

        // Delete from Firestore
        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(eventId)
                .delete();
        result.get(); // Wait for completion

        return true;
    }

    /**
     * Update event status
     */
    public Event updateEventStatus(String eventId, String coordinatorId, String status) 
            throws ExecutionException, InterruptedException {
        Event existingEvent = getEventById(eventId);
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }

        if (!existingEvent.getCoordinatorId().equals(coordinatorId)) {
            throw new SecurityException("You don't have permission to update this event");
        }

        existingEvent.setStatus(status);
        existingEvent.setUpdatedAt(Instant.now().toString());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", existingEvent.getUpdatedAt());

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(eventId)
                .update(updates);
        result.get();

        return existingEvent;
    }

    /**
     * Convert Event object to Map for Firestore
     */
    private Map<String, Object> convertEventToMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", event.getEventId());
        map.put("coordinatorId", event.getCoordinatorId());
        map.put("title", event.getTitle());
        map.put("venue", event.getVenue());
        map.put("dateTime", event.getDateTime());
        map.put("ticketsAvailable", event.getTicketsAvailable());
        map.put("ticketCategories", event.getTicketCategories());
        map.put("pastEventDetails", event.getPastEventDetails());
        map.put("eventImageUrls", event.getEventImageUrls());
        map.put("sellItems", event.getSellItems());
        map.put("description", event.getDescription());
        map.put("organizingDepartment", event.getOrganizingDepartment());
        map.put("status", event.getStatus());
        map.put("createdAt", event.getCreatedAt());
        map.put("updatedAt", event.getUpdatedAt());
        return map;
    }

    /**
     * Convert Firestore Map to Event object
     */
    private Event convertMapToEvent(String eventId, Map<String, Object> data) {
        Event event = new Event();
        event.setEventId(eventId);
        event.setCoordinatorId((String) data.get("coordinatorId"));
        event.setTitle((String) data.get("title"));
        event.setVenue((String) data.get("venue"));
        event.setDateTime((String) data.get("dateTime"));
        event.setTicketsAvailable((Boolean) data.get("ticketsAvailable"));
        event.setTicketCategories((List) data.get("ticketCategories"));
        event.setPastEventDetails((List) data.get("pastEventDetails"));
        event.setEventImageUrls((List) data.get("eventImageUrls"));
        event.setSellItems((List) data.get("sellItems"));
        event.setDescription((String) data.get("description"));
        event.setOrganizingDepartment((String) data.get("organizingDepartment"));
        event.setStatus((String) data.get("status"));
        
        // Handle timestamps - convert from Firestore Timestamp to String if needed
        Object createdAt = data.get("createdAt");
        if (createdAt instanceof com.google.cloud.Timestamp) {
            event.setCreatedAt(((com.google.cloud.Timestamp) createdAt).toDate().toInstant().toString());
        } else if (createdAt instanceof String) {
            event.setCreatedAt((String) createdAt);
        }
        
        Object updatedAt = data.get("updatedAt");
        if (updatedAt instanceof com.google.cloud.Timestamp) {
            event.setUpdatedAt(((com.google.cloud.Timestamp) updatedAt).toDate().toInstant().toString());
        } else if (updatedAt instanceof String) {
            event.setUpdatedAt((String) updatedAt);
        }
        
        return event;
    }
}
