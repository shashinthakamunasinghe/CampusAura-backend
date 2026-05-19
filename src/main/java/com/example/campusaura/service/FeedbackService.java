package com.example.campusaura.service;

import com.example.campusaura.dto.FeedbackDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private static final String COLLECTION_NAME = "event_feedback";

    @Autowired
    private Firestore firestore;

    /**
     * Get all feedback for an event, ordered by creation time
     */
    public List<FeedbackDTO> getFeedbackByEventId(String eventId) throws ExecutionException, InterruptedException {
        try {
            // Simple query without orderBy to avoid composite index requirement
            List<QueryDocumentSnapshot> documents = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("eventId", eventId)
                    .get().get().getDocuments();

            return documents.stream()
                    .map(this::documentToDTO)
                    .sorted((a, b) -> {
                        String dateA = a.getCreatedAt() != null ? a.getCreatedAt() : "";
                        String dateB = b.getCreatedAt() != null ? b.getCreatedAt() : "";
                        return dateA.compareTo(dateB);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // If index doesn't exist or collection is empty, return empty list
            System.err.println("Feedback query failed (likely missing index): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Add feedback for an event
     */
    public FeedbackDTO addFeedback(String eventId, String userId, String userName, String text) 
            throws ExecutionException, InterruptedException {
        String feedbackId = UUID.randomUUID().toString();
        String timestamp = Instant.now().toString();

        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("feedbackId", feedbackId);
        feedbackData.put("eventId", eventId);
        feedbackData.put("userId", userId);
        feedbackData.put("userName", userName);
        feedbackData.put("text", text);
        feedbackData.put("createdAt", timestamp);

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(feedbackId)
                .set(feedbackData);
        result.get();

        FeedbackDTO dto = new FeedbackDTO();
        dto.setFeedbackId(feedbackId);
        dto.setEventId(eventId);
        dto.setUserId(userId);
        dto.setUserName(userName);
        dto.setText(text);
        dto.setCreatedAt(timestamp);

        return dto;
    }

    private FeedbackDTO documentToDTO(QueryDocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        FeedbackDTO dto = new FeedbackDTO();
        dto.setFeedbackId((String) data.get("feedbackId"));
        dto.setEventId((String) data.get("eventId"));
        dto.setUserId((String) data.get("userId"));
        dto.setUserName((String) data.get("userName"));
        dto.setText((String) data.get("text"));

        Object createdAt = data.get("createdAt");
        if (createdAt instanceof com.google.cloud.Timestamp) {
            dto.setCreatedAt(((com.google.cloud.Timestamp) createdAt).toDate().toInstant().toString());
        } else if (createdAt instanceof String) {
            dto.setCreatedAt((String) createdAt);
        }

        return dto;
    }
}
