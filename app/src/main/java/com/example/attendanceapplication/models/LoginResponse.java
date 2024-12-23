package com.example.attendanceapplication.models;

public class LoginResponse {
    private String token;
    private User user;

    // Getters
    public String getToken() { return token; }
    public User getUser() { return user; }
}