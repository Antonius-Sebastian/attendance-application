package com.example.attendanceapplication;


import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class AttendanceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
