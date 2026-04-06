package com.example.campusaura.service;

import com.example.campusaura.dto.CoordinatorRequestDTO;
import com.example.campusaura.dto.CoordinatorResponseDTO;
import com.example.campusaura.model.Coordinator;
import com.example.campusaura.security.Roles;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CoordinatorService {

    @Autowired
    private Firestore firestore;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    private static final String COLLECTION_NAME = "coordinators";

    // Register a new coordinator
    public CoordinatorResponseDTO registerCoordinator(CoordinatorRequestDTO request) throws ExecutionException, InterruptedException {
        String firebaseUid = null;
        
        try {
            // Step 1: Create Firebase Auth user with email and password
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                        .setEmail(request.getEmail())
                        .setPassword(request.getPassword())
                        .setDisplayName(request.getFirstName() + " " + request.getLastName())
                        .setEmailVerified(true); // Auto-verify coordinator accounts
                
                UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
                firebaseUid = userRecord.getUid();
                
                // Step 2: Create user document in Firestore users collection with COORDINATOR role
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", firebaseUid);
                userData.put("email", request.getEmail());
                userData.put("name", request.getFirstName() + " " + request.getLastName());
                userData.put("role", Roles.COORDINATOR);
                userData.put("verified", true);
                userData.put("createdAt", com.google.cloud.Timestamp.now());
                
                firestore.collection("users").document(firebaseUid).set(userData).get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Firebase Auth user: " + e.getMessage(), e);
        }
        
        // Step 3: Create coordinator record in coordinators collection
        Coordinator coordinator = new Coordinator();
        coordinator.setFirstName(request.getFirstName());
        coordinator.setLastName(request.getLastName());
        coordinator.setPhoneNumber(request.getPhoneNumber());
        coordinator.setEmail(request.getEmail());
        
        // Set new fields
        coordinator.setDepartment(request.getDepartment());
        coordinator.setDegree(request.getDegree());
        coordinator.setShortIntroduction(request.getShortIntroduction());
        
        // Keep backward compatibility
        coordinator.setDegreeProgramme(request.getDegreeProgramme() != null ? 
            request.getDegreeProgramme() : request.getDepartment());
        
        coordinator.setActive(true);
        coordinator.setCreatedAt(LocalDateTime.now());
        coordinator.setUpdatedAt(LocalDateTime.now());

        // Add to Firestore coordinators collection
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
        coordinator.setId(docRef.getId());
        
        Map<String, Object> data = coordinatorToMap(coordinator);
        docRef.set(data).get();

        return coordinatorToDTO(coordinator, 0);
    }

    // Get all coordinators
    public List<CoordinatorResponseDTO> getAllCoordinators() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(this::documentToCoordinator)
                .map(coordinator -> {
                    try {
                        int eventCount = eventService.getEventCountByCoordinator(coordinator.getId());
                        return coordinatorToDTO(coordinator, eventCount);
                    } catch (ExecutionException | InterruptedException e) {
                        return coordinatorToDTO(coordinator, 0);
                    }
                })
                .collect(Collectors.toList());
    }

    // Get coordinator by ID
    public CoordinatorResponseDTO getCoordinatorById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME).document(id).get().get();
        
        if (!document.exists()) {
            throw new RuntimeException("Coordinator not found with id: " + id);
        }

        Coordinator coordinator = documentToCoordinator(document);
        int eventCount = eventService.getEventCountByCoordinator(id);
        return coordinatorToDTO(coordinator, eventCount);
    }

    // Update coordinator
    public CoordinatorResponseDTO updateCoordinator(String id, CoordinatorRequestDTO request) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Coordinator not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", request.getFirstName());
        updates.put("lastName", request.getLastName());
        updates.put("phoneNumber", request.getPhoneNumber());
        updates.put("email", request.getEmail());
        updates.put("department", request.getDepartment());
        updates.put("degree", request.getDegree());
        updates.put("shortIntroduction", request.getShortIntroduction());
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();

        Coordinator coordinator = documentToCoordinator(docRef.get().get());
        int eventCount = eventService.getEventCountByCoordinator(id);
        return coordinatorToDTO(coordinator, eventCount);
    }

    // Update coordinator status (active/inactive)
    public CoordinatorResponseDTO updateCoordinatorStatus(String id, boolean active) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Coordinator not found with id: " + id);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("active", active);
        updates.put("updatedAt", LocalDateTime.now().toString());

        docRef.update(updates).get();

        Coordinator coordinator = documentToCoordinator(docRef.get().get());
        int eventCount = eventService.getEventCountByCoordinator(id);
        return coordinatorToDTO(coordinator, eventCount);
    }

    // Delete coordinator
    public void deleteCoordinator(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            throw new RuntimeException("Coordinator not found with id: " + id);
        }

        docRef.delete().get();
    }

    // Helper methods
    private Map<String, Object> coordinatorToMap(Coordinator coordinator) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", coordinator.getId());
        data.put("firstName", coordinator.getFirstName());
        data.put("lastName", coordinator.getLastName());
        data.put("phoneNumber", coordinator.getPhoneNumber());
        data.put("email", coordinator.getEmail());
        data.put("department", coordinator.getDepartment());
        data.put("degree", coordinator.getDegree());
        data.put("shortIntroduction", coordinator.getShortIntroduction());
        data.put("degreeProgramme", coordinator.getDegreeProgramme()); // Legacy field
        data.put("active", coordinator.isActive());
        data.put("createdAt", coordinator.getCreatedAt().toString());
        data.put("updatedAt", coordinator.getUpdatedAt().toString());
        return data;
    }

    private Coordinator documentToCoordinator(DocumentSnapshot document) {
        Coordinator coordinator = new Coordinator();
        coordinator.setId(document.getId());
        coordinator.setFirstName(document.getString("firstName"));
        coordinator.setLastName(document.getString("lastName"));
        coordinator.setPhoneNumber(document.getString("phoneNumber"));
        coordinator.setEmail(document.getString("email"));
        coordinator.setDepartment(document.getString("department"));
        coordinator.setDegree(document.getString("degree"));
        coordinator.setShortIntroduction(document.getString("shortIntroduction"));
        coordinator.setDegreeProgramme(document.getString("degreeProgramme")); // Legacy field
        coordinator.setActive(document.getBoolean("active") != null ? document.getBoolean("active") : true);
        
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            coordinator.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }
        
        String updatedAtStr = document.getString("updatedAt");
        if (updatedAtStr != null) {
            coordinator.setUpdatedAt(LocalDateTime.parse(updatedAtStr));
        }
        
        return coordinator;
    }

    private CoordinatorResponseDTO coordinatorToDTO(Coordinator coordinator, int eventCount) {
        CoordinatorResponseDTO dto = new CoordinatorResponseDTO();
        dto.setId(coordinator.getId());
        dto.setFirstName(coordinator.getFirstName());
        dto.setLastName(coordinator.getLastName());
        dto.setPhoneNumber(coordinator.getPhoneNumber());
        dto.setEmail(coordinator.getEmail());
        dto.setDepartment(coordinator.getDepartment());
        dto.setDegree(coordinator.getDegree());
        dto.setShortIntroduction(coordinator.getShortIntroduction());
        dto.setDegreeProgramme(coordinator.getDegreeProgramme()); // Legacy field
        dto.setActive(coordinator.isActive());
        dto.setEventCount(eventCount);
        dto.setCreatedAt(coordinator.getCreatedAt());
        return dto;
    }
}
