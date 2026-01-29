package com.example.campusaura.model;

import java.util.List;

public class Event {
    private String eventId;
    private String coordinatorId;  // Firebase UID of the coordinator
    private String title;
    private String venue;
    private String dateTime;  // ISO 8601 format
    private Boolean ticketsAvailable;
    private List<TicketCategory> ticketCategories;
    private List<PastEventDetail> pastEventDetails;
    private List<String> eventImageUrls;
    private List<SellItem> sellItems;
    private String description;
    private String organizingDepartment;
    private String createdAt;
    private String updatedAt;
    private String status;  // e.g., "DRAFT", "PUBLISHED", "COMPLETED", "CANCELLED"

    public Event() {
    }

    public Event(String eventId, String coordinatorId, String title, String venue, String dateTime,
                 Boolean ticketsAvailable, List<TicketCategory> ticketCategories,
                 List<PastEventDetail> pastEventDetails, List<String> eventImageUrls,
                 List<SellItem> sellItems, String description, String organizingDepartment,
                 String createdAt, String updatedAt, String status) {
        this.eventId = eventId;
        this.coordinatorId = coordinatorId;
        this.title = title;
        this.venue = venue;
        this.dateTime = dateTime;
        this.ticketsAvailable = ticketsAvailable;
        this.ticketCategories = ticketCategories;
        this.pastEventDetails = pastEventDetails;
        this.eventImageUrls = eventImageUrls;
        this.sellItems = sellItems;
        this.description = description;
        this.organizingDepartment = organizingDepartment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getTicketsAvailable() {
        return ticketsAvailable;
    }

    public void setTicketsAvailable(Boolean ticketsAvailable) {
        this.ticketsAvailable = ticketsAvailable;
    }

    public List<TicketCategory> getTicketCategories() {
        return ticketCategories;
    }

    public void setTicketCategories(List<TicketCategory> ticketCategories) {
        this.ticketCategories = ticketCategories;
    }

    public List<PastEventDetail> getPastEventDetails() {
        return pastEventDetails;
    }

    public void setPastEventDetails(List<PastEventDetail> pastEventDetails) {
        this.pastEventDetails = pastEventDetails;
    }

    public List<String> getEventImageUrls() {
        return eventImageUrls;
    }

    public void setEventImageUrls(List<String> eventImageUrls) {
        this.eventImageUrls = eventImageUrls;
    }

    public List<SellItem> getSellItems() {
        return sellItems;
    }

    public void setSellItems(List<SellItem> sellItems) {
        this.sellItems = sellItems;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
