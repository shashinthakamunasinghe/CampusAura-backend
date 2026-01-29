package com.example.campusaura.model;

public class TicketCategory {
    private String categoryName;  // e.g., "Normal", "VIP", "VVIP"
    private Double price;

    public TicketCategory() {
    }

    public TicketCategory(String categoryName, Double price) {
        this.categoryName = categoryName;
        this.price = price;
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
}
