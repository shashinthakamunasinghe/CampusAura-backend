package com.example.campusaura.dto;

import java.util.List;

public class DashboardStatsDTO {
    private long totalEvents;
    private double eventsPercentageChange;
    private long activeUsers;
    private double usersPercentageChange;
    private long totalProducts;
    private double productsPercentageChange;
    private long productsSold;
    private boolean productsSoldIsNew;
    private List<EventResponseDTO> recentEvents;
    private List<TopCoordinatorDTO> topCoordinators;

    // Constructors
    public DashboardStatsDTO() {}

    // Getters and Setters
    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public double getEventsPercentageChange() {
        return eventsPercentageChange;
    }

    public void setEventsPercentageChange(double eventsPercentageChange) {
        this.eventsPercentageChange = eventsPercentageChange;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public double getUsersPercentageChange() {
        return usersPercentageChange;
    }

    public void setUsersPercentageChange(double usersPercentageChange) {
        this.usersPercentageChange = usersPercentageChange;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getProductsPercentageChange() {
        return productsPercentageChange;
    }

    public void setProductsPercentageChange(double productsPercentageChange) {
        this.productsPercentageChange = productsPercentageChange;
    }

    public long getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(long productsSold) {
        this.productsSold = productsSold;
    }

    public boolean isProductsSoldIsNew() {
        return productsSoldIsNew;
    }

    public void setProductsSoldIsNew(boolean productsSoldIsNew) {
        this.productsSoldIsNew = productsSoldIsNew;
    }

    public List<EventResponseDTO> getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(List<EventResponseDTO> recentEvents) {
        this.recentEvents = recentEvents;
    }

    public List<TopCoordinatorDTO> getTopCoordinators() {
        return topCoordinators;
    }

    public void setTopCoordinators(List<TopCoordinatorDTO> topCoordinators) {
        this.topCoordinators = topCoordinators;
    }
}
