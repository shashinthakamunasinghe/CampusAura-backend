package com.example.campusaura.dto;

public class TopCoordinatorDTO {
    private String id;
    private String name;
    private int eventCount;

    // Constructors
    public TopCoordinatorDTO() {}

    public TopCoordinatorDTO(String id, String name, int eventCount) {
        this.id = id;
        this.name = name;
        this.eventCount = eventCount;
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

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}
