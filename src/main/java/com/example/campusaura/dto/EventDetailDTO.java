package com.example.campusaura.dto;

import com.example.campusaura.model.PastEventDetail;
import com.example.campusaura.model.SellItem;
import com.example.campusaura.model.TicketCategory;

import java.util.List;

/**
 * DTO for complete event details shown on event detail page
 * Contains all information needed for the event detail view
 */
public class EventDetailDTO {
    private String eventId;
    private String title;
    private String description;
    private String venue;
    private String dateTime;
    private String category;
    private Integer attendeeCount;
    private List<String> eventImageUrls;
    private String organizingDepartment;
    
    // Ticket information
    private Boolean ticketsAvailable;
    private List<TicketCategory> ticketCategories;
    private Integer totalSpots;
    private Integer availableSpots;
    
    // Event schedule (from pastEventDetails)
    private List<ScheduleItem> schedule;
    
    // Event gallery (from pastEventDetails)
    private List<String> galleryImages;
    
    // Sponsors (from sellItems)
    private List<Sponsor> sponsors;
    
    // Event status
    private String status;

    public EventDetailDTO() {
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

    public Integer getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(Integer totalSpots) {
        this.totalSpots = totalSpots;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public List<ScheduleItem> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleItem> schedule) {
        this.schedule = schedule;
    }

    public List<String> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<String> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Inner classes for structured data
    public static class ScheduleItem {
        private String time;
        private String event;

        public ScheduleItem() {
        }

        public ScheduleItem(String time, String event) {
            this.time = time;
            this.event = event;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }
    }

    public static class Sponsor {
        private String tier;
        private String amount;
        private String logo;
        private String name;

        public Sponsor() {
        }

        public Sponsor(String tier, String amount, String logo, String name) {
            this.tier = tier;
            this.amount = amount;
            this.logo = logo;
            this.name = name;
        }

        public String getTier() {
            return tier;
        }

        public void setTier(String tier) {
            this.tier = tier;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
