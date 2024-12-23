package com.example.attendanceapplication.models;

public class AttendanceRequest {
    private int eventId;
    private int userId;

    public AttendanceRequest(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
    }
}