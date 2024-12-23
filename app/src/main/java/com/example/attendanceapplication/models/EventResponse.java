package com.example.attendanceapplication.models;

import com.google.gson.annotations.SerializedName;

public class EventResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("event")
    private Event event;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Event getEvent() { return event; }
}
