package com.example.attendanceapplication.models;

import java.util.List;

public class CreateEventRequest {
    private String title;
    private String description;
    private String date;
    private String startTime;
    private String endTime;
    private List<String> classes;

    public CreateEventRequest(String title, String description, String date,
                              String startTime, String endTime, List<String> classes) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classes = classes;
    }
}