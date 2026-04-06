package com.example.campusaura.model;

public class EventAccountDetails {
    private String accountName;
    private String accountNumber;
    private String email;
    private String phone;
    private String role;

    public EventAccountDetails() {
    }

    public EventAccountDetails(String accountName, String accountNumber, String email, String phone, String role) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
