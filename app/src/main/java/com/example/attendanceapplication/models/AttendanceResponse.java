package com.example.attendanceapplication.models;

// AttendanceResponse.java
public class AttendanceResponse {
    private int eventId;
    private int userId;
    private String entryStatus;  // "absent" or "present"
    private String exitStatus;   // "absent" or "present"
    private boolean success;
    private String message;

    // Getters
    public int getEventId() { return eventId; }
    public int getUserId() { return userId; }
    public String getEntryStatus() { return entryStatus; }
    public String getExitStatus() { return exitStatus; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}