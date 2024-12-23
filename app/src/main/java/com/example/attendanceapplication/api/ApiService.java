package com.example.attendanceapplication.api;

import com.example.attendanceapplication.models.AttendanceRecord;
import com.example.attendanceapplication.models.AttendanceRequest;
import com.example.attendanceapplication.models.AttendanceResponse;
import com.example.attendanceapplication.models.ClassResponse;
import com.example.attendanceapplication.models.CreateEventRequest;
import com.example.attendanceapplication.models.Event;
import com.example.attendanceapplication.models.EventResponse;
import com.example.attendanceapplication.models.LoginRequest;
import com.example.attendanceapplication.models.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("events")
    Call<List<Event>> getEvents(@Header("Authorization") String token);

    @POST("attendance/entry")
    Call<AttendanceResponse> recordEntry(
            @Header("Authorization") String token,
            @Body AttendanceRequest request
    );

    @POST("attendance/exit")
    Call<AttendanceResponse> recordExit(
            @Header("Authorization") String token,
            @Body AttendanceRequest request
    );

    @POST("events")
    Call<EventResponse> createEvent(
            @Header("Authorization") String token,
            @Body CreateEventRequest request
    );

    @GET("attendance/{eventId}")
    Call<List<AttendanceRecord>> getEventAttendance(
            @Header("Authorization") String token,
            @Path("eventId") int eventId
    );

    @GET("classes")
    Call<List<ClassResponse>> getClasses(@Header("Authorization") String token);
}