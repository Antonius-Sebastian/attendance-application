package com.example.attendanceapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendanceapplication.EventAdapter;
import com.example.attendanceapplication.R;
import com.example.attendanceapplication.activities.QRScannerActivity;
import com.example.attendanceapplication.api.ApiClient;
import com.example.attendanceapplication.api.ApiService;
import com.example.attendanceapplication.models.Event;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EventAdapter eventAdapter;
    private ApiService apiService;
    private FloatingActionButton fabScanner;
    private SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupFabScanner();
        setupSwipeRefresh();

        // Initialize API service and load events
        apiService = ApiClient.getClient().create(ApiService.class);
        loadEvents();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fabScanner = view.findViewById(R.id.fabScanner);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupFabScanner() {
        if (sharedPrefManager.isStaff()) {
            fabScanner.setVisibility(View.VISIBLE);
            fabScanner.setOnClickListener(v -> startActivity(new Intent(getContext(), QRScannerActivity.class)));
        } else {
            fabScanner.setVisibility(View.GONE);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadEvents);

        // Customize the refresh indicator colors
        swipeRefreshLayout.setColorSchemeResources(
                R.color.md_theme_primary,
                R.color.md_theme_secondary,
                R.color.md_theme_tertiary
        );
    }

    private void loadEvents() {
        swipeRefreshLayout.setRefreshing(true);
        String token = "Bearer " + sharedPrefManager.getToken();

        apiService.getEvents(token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    eventAdapter = new EventAdapter(getContext(), response.body(), sharedPrefManager.isStaff());
                    recyclerView.setAdapter(eventAdapter);
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

    private void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> loadEvents())
                    .show();
        }
    }
}