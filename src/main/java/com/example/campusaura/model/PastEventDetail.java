package com.example.campusaura.model;

import java.util.List;

public class PastEventDetail {
    private String eventId;
    private String title;
    private String description;
    private String date;
    private List<String> imageUrls;
    private String outcome;

    public PastEventDetail() {
    }

    public PastEventDetail(String eventId, String title, String description, String date, List<String> imageUrls, String outcome) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.imageUrls = imageUrls;
        this.outcome = outcome;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
