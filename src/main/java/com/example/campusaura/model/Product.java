package com.example.campusaura.model;

import java.time.LocalDateTime;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;
    private String sellerId;
    private String sellerName;
    private ProductStatus status; // PENDING, APPROVED, AVAILABLE, SOLD, DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime soldAt;

    // Enum
    public enum ProductStatus {
        PENDING,    // Awaiting admin approval
        APPROVED,   // Approved by admin but not sold yet (same as AVAILABLE)
        AVAILABLE,  // Available for purchase
        SOLD,       // Successfully sold
        DELETED     // Deleted/Disabled by admin or user
    }

    // Constructors
    public Product() {
        this.status = ProductStatus.PENDING; // New products start as PENDING
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }
}
