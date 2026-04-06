package com.example.campusaura.dto;

import java.util.List;

public class PaymentStatsDTO {
    private double ticketRevenue;
    private double marketplaceRevenue;
    private double totalRevenue;
    private List<TransactionResponseDTO> recentTransactions;

    // Constructors
    public PaymentStatsDTO() {}

    public PaymentStatsDTO(double ticketRevenue, double marketplaceRevenue, 
                          List<TransactionResponseDTO> recentTransactions) {
        this.ticketRevenue = ticketRevenue;
        this.marketplaceRevenue = marketplaceRevenue;
        this.totalRevenue = ticketRevenue + marketplaceRevenue;
        this.recentTransactions = recentTransactions;
    }

    // Getters and Setters
    public double getTicketRevenue() {
        return ticketRevenue;
    }

    public void setTicketRevenue(double ticketRevenue) {
        this.ticketRevenue = ticketRevenue;
        this.totalRevenue = this.ticketRevenue + this.marketplaceRevenue;
    }

    public double getMarketplaceRevenue() {
        return marketplaceRevenue;
    }

    public void setMarketplaceRevenue(double marketplaceRevenue) {
        this.marketplaceRevenue = marketplaceRevenue;
        this.totalRevenue = this.ticketRevenue + this.marketplaceRevenue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<TransactionResponseDTO> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponseDTO> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
