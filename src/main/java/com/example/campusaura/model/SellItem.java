package com.example.campusaura.model;

import java.util.List;

public class SellItem {
    private String itemName;
    private String description;
    private Double price;
    private List<String> imageUrls;  // URLs of item images

    public SellItem() {
    }

    public SellItem(String itemName, String description, Double price, List<String> imageUrls) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
