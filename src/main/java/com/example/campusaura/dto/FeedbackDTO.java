package com.example.campusaura.dto;

public class FeedbackDTO {
    private String feedbackId;
    private String eventId;
    private String userId;
    private String userName;
    private String text;
    private String createdAt;

    public FeedbackDTO() {}

    public FeedbackDTO(String feedbackId, String eventId, String userId, String userName, String text, String createdAt) {
        this.feedbackId = feedbackId;
        this.eventId = eventId;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
