package com.example.campusaura.dto;

import java.util.List;

public class ProductSaleDTO {
    private String saleId;
    private String userId;
    private String userName;
    private String userEmail;
    private List<SaleItem> items;
    private double totalAmount;
    private String stripePaymentId;
    private String purchasedAt;

    public ProductSaleDTO() {}

    // Getters and Setters
    public String getSaleId() { return saleId; }
    public void setSaleId(String saleId) { this.saleId = saleId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStripePaymentId() { return stripePaymentId; }
    public void setStripePaymentId(String stripePaymentId) { this.stripePaymentId = stripePaymentId; }
    public String getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(String purchasedAt) { this.purchasedAt = purchasedAt; }

    public static class SaleItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;

        public SaleItem() {}

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}
