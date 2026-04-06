package com.example.campusaura.model;

public class EventScheduleItem {
    private String id;
    private String title;
    private String time;
    private String duration;

    public EventScheduleItem() {
    }

    public EventScheduleItem(String id, String title, String time, String duration) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
