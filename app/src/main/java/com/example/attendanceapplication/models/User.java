// User.java
package com.example.attendanceapplication.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String role;
    private Integer classId;
    private String className;  // Added for class name from API

    // Constructor used by SharedPrefManager
    public User(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Full constructor for API responses
    public User(int id, String name, String email, String role, Integer classId, String className) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.classId = classId;
        this.className = className;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Integer getClassId() { return classId; }
    public String getClassName() { return className; }
}