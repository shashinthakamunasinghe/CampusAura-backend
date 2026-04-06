package com.example.campusaura.dto;

import com.example.campusaura.model.Event;
import com.example.campusaura.model.TicketCategory;
import com.example.campusaura.model.PastEventDetail;
import com.example.campusaura.model.SellItem;

import java.util.List;

public class EventResponseDTO {
    private String eventId;
    private String coordinatorId;
    private String title;
    private String venue;
    private String dateTime;
    private Boolean ticketsAvailable;
    private String description;
    private String organizingDepartment;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String message;
    private List<TicketCategory> ticketCategories;
    private List<PastEventDetail> pastEventDetails;
    private List<String> eventImageUrls;
    private List<SellItem> sellItems;

    public EventResponseDTO() {
    }

    public EventResponseDTO(Event event, String message) {
        if (event != null) {
            this.eventId = event.getEventId();
            this.coordinatorId = event.getCoordinatorId();
            this.title = event.getTitle();
            this.venue = event.getVenue();
            this.dateTime = event.getDateTime();
            this.ticketsAvailable = event.getTicketsAvailable();
            this.description = event.getDescription();
            this.organizingDepartment = event.getOrganizingDepartment();
            this.status = event.getStatus();
            this.createdAt = event.getCreatedAt();
            this.updatedAt = event.getUpdatedAt();
        }
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
