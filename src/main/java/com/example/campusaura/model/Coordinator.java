package com.example.campusaura.model;

import java.time.LocalDateTime;

public class Coordinator {
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String department;
    private String degree;
    private String shortIntroduction;
    private String degreeProgramme; // Legacy field
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Coordinator() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Coordinator(String id, String firstName, String lastName, String phoneNumber, 
                       String email, String department, String degree, String shortIntroduction) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.department = department;
        this.degree = degree;
        this.shortIntroduction = shortIntroduction;
        this.active = true;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDegreeProgramme() {
        return degreeProgramme;
    }

    public void setDegreeProgramme(String degreeProgramme) {
        this.degreeProgramme = degreeProgramme;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getShortIntroduction() {
        return shortIntroduction;
    }

    public void setShortIntroduction(String shortIntroduction) {
        this.shortIntroduction = shortIntroduction;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
}
