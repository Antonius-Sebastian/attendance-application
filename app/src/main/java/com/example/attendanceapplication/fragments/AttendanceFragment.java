package com.example.attendanceapplication.fragments;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapplication.AttendanceAdapter;
import com.example.attendanceapplication.GroupedAttendanceAdapter;
import com.example.attendanceapplication.R;
import com.example.attendanceapplication.api.ApiClient;
import com.example.attendanceapplication.api.ApiService;
import com.example.attendanceapplication.models.AttendanceRecord;
import com.example.attendanceapplication.models.Event;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AttendanceFragment extends Fragment {
    private Spinner eventSpinner;
    private RecyclerView attendanceRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private List<Event> events;
    private Event currentSelectedEvent;
    private GroupedAttendanceAdapter attendanceAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        initializeViews(view);
        setupSwipeRefresh();
        loadEvents();

        return view;
    }

    private void initializeViews(View view) {
        eventSpinner = view.findViewById(R.id.eventSpinner);
        attendanceRecyclerView = view.findViewById(R.id.attendanceRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup spinner listener
        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedEvent = events.get(position);
                loadAttendance(currentSelectedEvent.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (currentSelectedEvent != null) {
                loadAttendance(currentSelectedEvent.getId());
            } else {
                loadEvents();
            }
        });

        // Customize the refresh indicator colors (optional)
        swipeRefreshLayout.setColorSchemeResources(
                R.color.md_theme_primary,
                R.color.md_theme_secondary,
                R.color.md_theme_tertiary
        );
    }

    private void loadEvents() {
        swipeRefreshLayout.setRefreshing(true);
        String token = "Bearer " + SharedPrefManager.getInstance(getContext()).getToken();

        apiService.getEvents(token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body();
                    setupEventSpinner();
                } else {
                    showError("Failed to load events");
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void loadAttendance(int eventId) {
        swipeRefreshLayout.setRefreshing(true);
        String token = "Bearer " + SharedPrefManager.getInstance(getContext()).getToken();

        apiService.getEventAttendance(token, eventId).enqueue(new Callback<List<AttendanceRecord>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    attendanceAdapter = new GroupedAttendanceAdapter(requireContext(), response.body());
                    attendanceRecyclerView.setAdapter(attendanceAdapter);
                } else {
                    showError("Failed to load attendance");
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void setupEventSpinner() {
        ArrayAdapter<Event> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, events);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(adapter);
    }

    private void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}