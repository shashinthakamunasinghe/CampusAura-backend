package com.example.campusaura.dto;

public class CoordinatorRequestDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String department;
    private String degree;
    private String shortIntroduction;
    private String password;
    
    // Legacy field - keep for backward compatibility
    private String degreeProgramme;

    // Constructors
    public CoordinatorRequestDTO() {}

    public CoordinatorRequestDTO(String firstName, String lastName, String phoneNumber, 
                                 String email, String department, String degree, 
                                 String shortIntroduction, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.department = department;
        this.degree = degree;
        this.shortIntroduction = shortIntroduction;
        this.password = password;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
