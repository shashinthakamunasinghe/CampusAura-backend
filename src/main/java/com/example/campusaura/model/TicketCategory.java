package com.example.campusaura.model;

public class TicketCategory {
    private String categoryName;  // e.g., "Normal", "VIP", "VVIP"
    private Double price;
    private Integer availableCount;  // Number of tickets available in this category

    public TicketCategory() {
    }

    public TicketCategory(String categoryName, Double price, Integer availableCount) {
        this.categoryName = categoryName;
        this.price = price;
        this.availableCount = availableCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }
}
