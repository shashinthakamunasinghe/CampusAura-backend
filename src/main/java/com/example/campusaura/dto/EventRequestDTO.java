package com.example.campusaura.dto;

import com.example.campusaura.model.PastEventDetail;
import com.example.campusaura.model.SellItem;
import com.example.campusaura.model.TicketCategory;

import java.util.List;

public class EventRequestDTO {
    private String title;
    private String venue;
    private String dateTime;
    private Boolean ticketsAvailable;
    private List<TicketCategory> ticketCategories;
    private List<PastEventDetail> pastEventDetails;
    private List<String> eventImageUrls;
    private List<SellItem> sellItems;
    private String description;
    private String organizingDepartment;
    private String status;

    public EventRequestDTO() {
    }

    public EventRequestDTO(String title, String venue, String dateTime, Boolean ticketsAvailable,
                           List<TicketCategory> ticketCategories, List<PastEventDetail> pastEventDetails,
                           List<String> eventImageUrls, List<SellItem> sellItems, String description,
                           String organizingDepartment, String status) {
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
        this.status = status;
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
