package com.example.campusaura.dto;

/**
 * DTO for admin event management
 * Includes event details along with coordinator name
 */
public class AdminEventDTO {
    private String eventId;
    private String title;
    private String coordinatorId;
    private String coordinatorName;  // Full name of the coordinator
    private String venue;
    private String dateTime;
    private String description;
    private String organizingDepartment;
    private String status;  // PENDING, APPROVED, REJECTED, etc.
    private String category;
    private String createdAt;
    private String updatedAt;
    private Integer attendeeCount;

    public AdminEventDTO() {
    }

    public AdminEventDTO(String eventId, String title, String coordinatorId, String coordinatorName,
                         String venue, String dateTime, String description, String organizingDepartment,
                         String status, String category, String createdAt, String updatedAt, Integer attendeeCount) {
        this.eventId = eventId;
        this.title = title;
        this.coordinatorId = coordinatorId;
        this.coordinatorName = coordinatorName;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
        this.organizingDepartment = organizingDepartment;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizingDepartment() {
        return organizingDepartment;
    }

    public void setOrganizingDepartment(String organizingDepartment) {
        this.organizingDepartment = organizingDepartment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(Integer attendeeCount) {
        this.attendeeCount = attendeeCount;
    }
}
