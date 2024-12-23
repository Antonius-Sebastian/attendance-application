package com.example.attendanceapplication.models;

import com.google.gson.annotations.SerializedName;

public class AttendanceRecord {
    @SerializedName("student_name")
    private String studentName;

    @SerializedName("class_name")
    private String className;

    @SerializedName("entry_status")
    private String entryStatus;

    @SerializedName("exit_status")
    private String exitStatus;

    // Getters
    public String getStudentName() { return studentName; }
    public String getClassName() { return className; }
    public String getEntryStatus() { return entryStatus; }
    public String getExitStatus() { return exitStatus; }
}