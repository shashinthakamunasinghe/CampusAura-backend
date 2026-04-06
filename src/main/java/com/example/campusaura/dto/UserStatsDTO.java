package com.example.campusaura.dto;

public class UserStatsDTO {
    private long totalUniversityStudents;
    private long totalExternalUsers;
    private long totalPendingVerification;

    // Constructors
    public UserStatsDTO() {}

    public UserStatsDTO(long totalUniversityStudents, long totalExternalUsers, long totalPendingVerification) {
        this.totalUniversityStudents = totalUniversityStudents;
        this.totalExternalUsers = totalExternalUsers;
        this.totalPendingVerification = totalPendingVerification;
    }

    // Getters and Setters
    public long getTotalUniversityStudents() {
        return totalUniversityStudents;
    }

    public void setTotalUniversityStudents(long totalUniversityStudents) {
        this.totalUniversityStudents = totalUniversityStudents;
    }

    public long getTotalExternalUsers() {
        return totalExternalUsers;
    }

    public void setTotalExternalUsers(long totalExternalUsers) {
        this.totalExternalUsers = totalExternalUsers;
    }

    public long getTotalPendingVerification() {
        return totalPendingVerification;
    }

    public void setTotalPendingVerification(long totalPendingVerification) {
        this.totalPendingVerification = totalPendingVerification;
    }
}
