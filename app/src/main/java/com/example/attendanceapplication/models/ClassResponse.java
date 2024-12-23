package com.example.attendanceapplication.models;

import com.google.gson.annotations.SerializedName;

public class ClassResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public int getId() { return id; }
    public String getName() { return name; }
}