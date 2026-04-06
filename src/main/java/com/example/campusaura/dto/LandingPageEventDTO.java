package com.example.campusaura.dto;

import java.util.List;

/**
 * DTO for landing page carousel events
 * Contains minimal event information for display
 */
public class LandingPageEventDTO {
    private String eventId;
    private String title;
    private String description;
    private String venue;
    private String dateTime;
    private List<String> eventImageUrls;
    private String organizingDepartment;
    private String category;
    private Integer attendeeCount;

    public LandingPageEventDTO() {
    }

    public LandingPageEventDTO(String eventId, String title, String description, String venue, 
                               String dateTime, List<String> eventImageUrls, String organizingDepartment,
                               String category, Integer attendeeCount) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.dateTime = dateTime;
        this.eventImageUrls = eventImageUrls;
        this.organizingDepartment = organizingDepartment;
        this.category = category;
        this.attendeeCount = attendeeCount;
    }

    // Getters and Setters
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getEventImageUrls() {
        return eventImageUrls;
    }

    public void setEventImageUrls(List<String> eventImageUrls) {
        this.eventImageUrls = eventImageUrls;
    }

    public String getOrganizingDepartment() {
        return organizingDepartment;
    }

    public void setOrganizingDepartment(String organizingDepartment) {
        this.organizingDepartment = organizingDepartment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(Integer attendeeCount) {
        this.attendeeCount = attendeeCount;
    }
}
