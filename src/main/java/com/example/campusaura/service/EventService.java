package com.example.campusaura.service;

import com.example.campusaura.dto.AdminEventDTO;
import com.example.campusaura.dto.EventDetailDTO;
import com.example.campusaura.dto.EventRequestDTO;
import com.example.campusaura.dto.EventResponseDTO;
import com.example.campusaura.dto.LandingPageEventDTO;
import com.example.campusaura.model.Coordinator;
import com.example.campusaura.model.Event;
import com.example.campusaura.model.EventAccountDetails;
import com.example.campusaura.model.EventScheduleItem;
import com.example.campusaura.model.PastEventDetail;
import com.example.campusaura.model.SellItem;
import com.example.campusaura.model.TicketCategory;
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
        event.setCategory(eventRequest.getCategory());
        event.setAttendeeCount(0);  // Initialize with 0 attendees
        event.setCreatedAt(timestamp);
        event.setUpdatedAt(timestamp);
        event.setSchedule(eventRequest.getSchedule());
        event.setAccountDetails(eventRequest.getAccountDetails());

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
     * Get event by ID (returns DTO)
     */
    public EventResponseDTO getEventByIdDTO(String eventId) throws ExecutionException, InterruptedException {
        Event event = getEventById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        return eventToResponseDTO(event);
    }

    /**
     * Get event by ID (returns Event object)
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
     * Get all events (returns Event objects)
     */
    public List<Event> getAllEventsInternal() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
    }

    /**
     * Get all events (returns DTOs)
     */
    public List<EventResponseDTO> getAllEvents() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .map(this::eventToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get events by category
     */
    public List<EventResponseDTO> getEventsByCategory(String category) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("category", category);

        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .map(this::eventToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete event
     */
    public void deleteEvent(String eventId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(eventId);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }

        docRef.delete().get();
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
        if (eventRequest.getCategory() != null) {
            existingEvent.setCategory(eventRequest.getCategory());
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
        map.put("category", event.getCategory());
        map.put("attendeeCount", event.getAttendeeCount());
        map.put("createdAt", event.getCreatedAt());
        map.put("updatedAt", event.getUpdatedAt());
        map.put("schedule", event.getSchedule());
        map.put("accountDetails", event.getAccountDetails());
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
        
        // Convert ticketCategories from List<Map> to List<TicketCategory>
        List<com.example.campusaura.model.TicketCategory> ticketCategories = new ArrayList<>();
        Object ticketCategoriesObj = data.get("ticketCategories");
        if (ticketCategoriesObj instanceof List) {
            for (Object item : (List) ticketCategoriesObj) {
                if (item instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    com.example.campusaura.model.TicketCategory category = new com.example.campusaura.model.TicketCategory();
                    category.setCategoryName((String) map.get("categoryName"));
                    Object price = map.get("price");
                    if (price instanceof Number) {
                        category.setPrice(((Number) price).doubleValue());
                    }
                    Object availableCount = map.get("availableCount");
                    if (availableCount instanceof Number) {
                        category.setAvailableCount(((Number) availableCount).intValue());
                    }
                    ticketCategories.add(category);
                }
            }
        }
        event.setTicketCategories(ticketCategories);
        
        // Convert pastEventDetails from List<Map> to List<PastEventDetail>
        List<PastEventDetail> pastEventDetails = new ArrayList<>();
        Object pastEventDetailsObj = data.get("pastEventDetails");
        if (pastEventDetailsObj instanceof List) {
            for (Object item : (List) pastEventDetailsObj) {
                if (item instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    PastEventDetail detail = new PastEventDetail();
                    detail.setEventId((String) map.get("eventId"));
                    detail.setTitle((String) map.get("title"));
                    detail.setDescription((String) map.get("description"));
                    detail.setDate((String) map.get("date"));
                    detail.setImageUrls((List<String>) map.get("imageUrls"));
                    detail.setOutcome((String) map.get("outcome"));
                    pastEventDetails.add(detail);
                }
            }
        }
        event.setPastEventDetails(pastEventDetails);
        
        event.setEventImageUrls((List) data.get("eventImageUrls"));
        
        // Convert sellItems from List<Map> to List<SellItem>
        List<SellItem> sellItems = new ArrayList<>();
        Object sellItemsObj = data.get("sellItems");
        if (sellItemsObj instanceof List) {
            for (Object item : (List) sellItemsObj) {
                if (item instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    SellItem sellItem = new SellItem();
                    sellItem.setItemName((String) map.get("itemName"));
                    sellItem.setDescription((String) map.get("description"));
                    Object price = map.get("price");
                    if (price instanceof Number) {
                        sellItem.setPrice(((Number) price).doubleValue());
                    }
                    sellItem.setImageUrls((List<String>) map.get("imageUrls"));
                    sellItems.add(sellItem);
                }
            }
        }
        event.setSellItems(sellItems);
        
        event.setDescription((String) data.get("description"));
        event.setOrganizingDepartment((String) data.get("organizingDepartment"));
        event.setStatus((String) data.get("status"));
        event.setCategory((String) data.get("category"));
        
        // Handle attendeeCount - convert from Long to Integer if needed
        Object attendeeCount = data.get("attendeeCount");
        if (attendeeCount instanceof Long) {
            event.setAttendeeCount(((Long) attendeeCount).intValue());
        } else if (attendeeCount instanceof Integer) {
            event.setAttendeeCount((Integer) attendeeCount);
        } else {
            event.setAttendeeCount(0);
        }
        
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
        // Convert schedule from List<Map> to List<EventScheduleItem>
        List<EventScheduleItem> scheduleItems = new ArrayList<>();
        Object scheduleObj = data.get("schedule");
        if (scheduleObj instanceof List) {
            for (Object item : (List) scheduleObj) {
                if (item instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    EventScheduleItem scheduleItem = new EventScheduleItem();
                    scheduleItem.setId((String) map.get("id"));
                    scheduleItem.setTitle((String) map.get("title"));
                    scheduleItem.setTime((String) map.get("time"));
                    scheduleItem.setDuration((String) map.get("duration"));
                    scheduleItems.add(scheduleItem);
                }
            }
        }
        event.setSchedule(scheduleItems);
        
        // Convert accountDetails from Map to EventAccountDetails
        Object accountDetailsObj = data.get("accountDetails");
        if (accountDetailsObj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) accountDetailsObj;
            EventAccountDetails accountDetails = new EventAccountDetails();
            accountDetails.setAccountName((String) map.get("accountName"));
            accountDetails.setAccountNumber((String) map.get("accountNumber"));
            accountDetails.setEmail((String) map.get("email"));
            accountDetails.setPhone((String) map.get("phone"));
            accountDetails.setRole((String) map.get("role"));
            event.setAccountDetails(accountDetails);
        }
        
        }
        
        return event;
    }

    /**
     * Get recent events
     */
    public List<EventResponseDTO> getRecentEvents(int limit) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .map(this::eventToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get random ongoing events for landing page carousel
     */
    public List<LandingPageEventDTO> getRandomOngoingEvents(int limit) throws ExecutionException, InterruptedException {
        // Query for events with status "PUBLISHED" or "ONGOING"
        Query query = firestore.collection(COLLECTION_NAME)
                .whereIn("status", Arrays.asList("PUBLISHED", "ONGOING"));
        
        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();
        
        // Convert to Event objects
        List<Event> events = documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .collect(Collectors.toList());
        
        // Shuffle and return limited results as DTOs
        Collections.shuffle(events);
        return events.stream()
                .limit(limit)
                .map(this::eventToLandingPageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get latest published events for landing page
     */
    public List<LandingPageEventDTO> getLatestEvents(int limit) throws ExecutionException, InterruptedException {
        // Query for events with status "PUBLISHED" or "ONGOING"
        Query query = firestore.collection(COLLECTION_NAME)
                .whereIn("status", Arrays.asList("PUBLISHED", "ONGOING"));
        
        List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();
        
        // Convert to Event objects and sort by dateTime descending
        List<Event> events = documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .filter(event -> event.getDateTime() != null) // Filter out events without dateTime
                .sorted((e1, e2) -> e2.getDateTime().compareTo(e1.getDateTime())) // Sort descending (latest first)
                .collect(Collectors.toList());
        
        // Return limited results as DTOs
        return events.stream()
                .limit(limit)
                .map(this::eventToLandingPageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all published events for public events page
     * Supports category filtering and sorting
     */
    public List<LandingPageEventDTO> getPublicEvents(String category, String sortBy) throws ExecutionException, InterruptedException {
        // Get all events (no status filter for now to include all events)
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments();
        
        // Convert to Event objects
        List<Event> events = documents.stream()
                .map(doc -> convertMapToEvent(doc.getId(), doc.getData()))
                .filter(event -> {
                    // Include events with PUBLISHED, ONGOING status, or events without status (for testing)
                    String status = event.getStatus();
                    return status == null || 
                           status.equalsIgnoreCase("PUBLISHED") || 
                           status.equalsIgnoreCase("ONGOING") ||
                           status.equalsIgnoreCase("DRAFT"); // Include DRAFT for testing
                })
                .collect(Collectors.toList());
        
        // Filter by category if specified (and not "All")
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("All")) {
            events = events.stream()
                    .filter(event -> category.equalsIgnoreCase(event.getCategory()))
                    .collect(Collectors.toList());
        }
        
        // Sort events based on sortBy parameter
        if ("upcoming".equalsIgnoreCase(sortBy)) {
            // Sort by dateTime ascending (upcoming first), handle nulls
            events.sort((e1, e2) -> {
                String date1 = e1.getDateTime();
                String date2 = e2.getDateTime();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date1.compareTo(date2);
            });
        } else if ("latest".equalsIgnoreCase(sortBy)) {
            // Sort by createdAt descending (latest created first)
            events.sort((e1, e2) -> {
                String date1 = e1.getCreatedAt();
                String date2 = e2.getCreatedAt();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1);
            });
        } else if ("popular".equalsIgnoreCase(sortBy)) {
            // Sort by attendeeCount descending (most popular first)
            events.sort((e1, e2) -> {
                Integer count1 = e1.getAttendeeCount() != null ? e1.getAttendeeCount() : 0;
                Integer count2 = e2.getAttendeeCount() != null ? e2.getAttendeeCount() : 0;
                return count2.compareTo(count1);
            });
        } else {
            // Default: sort by createdAt descending (newest first)
            events.sort((e1, e2) -> {
                String date1 = e1.getCreatedAt();
                String date2 = e2.getCreatedAt();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1);
            });
        }
        
        // Convert to DTOs
        return events.stream()
                .map(this::eventToLandingPageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Event to LandingPageEventDTO
     */
    private LandingPageEventDTO eventToLandingPageDTO(Event event) {
        LandingPageEventDTO dto = new LandingPageEventDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setVenue(event.getVenue());
        dto.setDateTime(event.getDateTime());
        dto.setEventImageUrls(event.getEventImageUrls());
        dto.setOrganizingDepartment(event.getOrganizingDepartment());
        dto.setCategory(event.getCategory());
        dto.setAttendeeCount(event.getAttendeeCount() != null ? event.getAttendeeCount() : 0);
        return dto;
    }

    /**
     * Convert Event to EventResponseDTO
     */
    private EventResponseDTO eventToResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setVenue(event.getVenue());
        dto.setDateTime(event.getDateTime());
        dto.setTicketsAvailable(event.getTicketsAvailable());
        dto.setTicketCategories(event.getTicketCategories());
        dto.setPastEventDetails(event.getPastEventDetails());
        dto.setEventImageUrls(event.getEventImageUrls());
        dto.setSellItems(event.getSellItems());
        dto.setDescription(event.getDescription());
        dto.setOrganizingDepartment(event.getOrganizingDepartment());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

    /**
     * Update event status (for admin approval/rejection)
     */
    public EventResponseDTO updateEventStatus(String eventId, String status) 
            throws ExecutionException, InterruptedException {
        Event existingEvent = getEventById(eventId);
        if (existingEvent == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
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

        return eventToResponseDTO(existingEvent);
    }

    /**
     * Get count of pending events (for admin dashboard)
     */
    public long getPendingEventsCount() throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("status", "PENDING");
        ApiFuture<QuerySnapshot> future = query.get();
        return future.get().getDocuments().size();
    }

    /**
     * Get event count by coordinator ID
     */
    public int getEventCountByCoordinator(String coordinatorId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("coordinatorId", coordinatorId);
        ApiFuture<QuerySnapshot> future = query.get();
        return future.get().getDocuments().size();
    }

    /**
     * Get full event details for public event detail page
     */
    public EventDetailDTO getEventDetailById(String eventId) throws ExecutionException, InterruptedException {
        Event event = getEventById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        return eventToDetailDTO(event);
    }

    /**
     * Get all events for admin with coordinator names
     */
    public List<AdminEventDTO> getAllEventsForAdmin() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                .get()
                .get()
                .getDocuments();

        List<AdminEventDTO> adminEvents = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Event event = convertMapToEvent(doc.getId(), doc.getData());
            String coordinatorName = getCoordinatorName(event.getCoordinatorId());
            AdminEventDTO adminEventDTO = eventToAdminEventDTO(event, coordinatorName);
            adminEvents.add(adminEventDTO);
        }

        return adminEvents;
    }

    /**
     * Get coordinator name by ID
     */
    private String getCoordinatorName(String coordinatorId) {
        try {
            DocumentSnapshot doc = firestore.collection("coordinators")
                    .document(coordinatorId)
                    .get()
                    .get();
            
            if (doc.exists()) {
                String firstName = (String) doc.getData().get("firstName");
                String lastName = (String) doc.getData().get("lastName");
                return firstName + " " + lastName;
            }
            return "Unknown Coordinator";
        } catch (Exception e) {
            return "Unknown Coordinator";
        }
    }

    /**
     * Convert Event to AdminEventDTO
     */
    private AdminEventDTO eventToAdminEventDTO(Event event, String coordinatorName) {
        AdminEventDTO dto = new AdminEventDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setCoordinatorId(event.getCoordinatorId());
        dto.setCoordinatorName(coordinatorName);
        dto.setVenue(event.getVenue());
        dto.setDateTime(event.getDateTime());
        dto.setDescription(event.getDescription());
        dto.setOrganizingDepartment(event.getOrganizingDepartment());
        dto.setStatus(event.getStatus());
        dto.setCategory(event.getCategory());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setAttendeeCount(event.getAttendeeCount());
        return dto;
    }

    /**
     * Convert Event to EventDetailDTO with all details for event detail page
     */
    private EventDetailDTO eventToDetailDTO(Event event) {
        EventDetailDTO dto = new EventDetailDTO();
        
        // Basic event information
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setVenue(event.getVenue());
        dto.setDateTime(event.getDateTime());
        dto.setCategory(event.getCategory());
        dto.setAttendeeCount(event.getAttendeeCount() != null ? event.getAttendeeCount() : 0);
        dto.setEventImageUrls(event.getEventImageUrls());
        dto.setOrganizingDepartment(event.getOrganizingDepartment());
        dto.setStatus(event.getStatus());
        
        // Ticket information
        dto.setTicketsAvailable(event.getTicketsAvailable());
        dto.setTicketCategories(event.getTicketCategories());
        dto.setTotalSpots(500); // Default total spots
        dto.setAvailableSpots(180); // Default available spots (can be calculated from actual bookings)
        
        // Extract schedule from pastEventDetails if available
        List<EventDetailDTO.ScheduleItem> schedule = new ArrayList<>();
        if (event.getPastEventDetails() != null && !event.getPastEventDetails().isEmpty()) {
            // For now, we'll create default schedule items
            // You can enhance this to extract from pastEventDetails structure
            schedule.add(new EventDetailDTO.ScheduleItem("10:00 AM", "Event Opens"));
            schedule.add(new EventDetailDTO.ScheduleItem("11:00 AM", "Main Program"));
            schedule.add(new EventDetailDTO.ScheduleItem("1:00 PM", "Networking Session"));
        }
        dto.setSchedule(schedule);
        
        // Extract gallery images from pastEventDetails
        List<String> galleryImages = new ArrayList<>();
        if (event.getPastEventDetails() != null) {
            for (PastEventDetail detail : event.getPastEventDetails()) {
                if (detail.getImageUrls() != null) {
                    galleryImages.addAll(detail.getImageUrls());
                }
            }
        }
        dto.setGalleryImages(galleryImages);
        
        // Convert sellItems to sponsors
        List<EventDetailDTO.Sponsor> sponsors = new ArrayList<>();
        if (event.getSellItems() != null && !event.getSellItems().isEmpty()) {
            for (SellItem item : event.getSellItems()) {
                EventDetailDTO.Sponsor sponsor = new EventDetailDTO.Sponsor();
                sponsor.setName(item.getItemName());
                sponsor.setTier("Standard");
                sponsor.setAmount(item.getPrice() != null ? "$" + item.getPrice() : "TBA");
                sponsor.setLogo("🏢"); // Default logo
                sponsors.add(sponsor);
            }
        }
        // If no sponsors from sellItems, add default sponsors
        if (sponsors.isEmpty()) {
            sponsors.add(new EventDetailDTO.Sponsor("Platinum", "$10,000+", "🏢", "Platinum Sponsor"));
            sponsors.add(new EventDetailDTO.Sponsor("Gold", "$5,000+", "⭐", "Gold Sponsor"));
            sponsors.add(new EventDetailDTO.Sponsor("Silver", "$1,000+", "🎯", "Silver Sponsor"));
        }
        dto.setSponsors(sponsors);
        
        return dto;
    }
}
