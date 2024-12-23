package com.example.attendanceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.attendanceapplication.R;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Check if user is logged in
        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title based on role
        if (sharedPrefManager.isStaff()) {
            getSupportActionBar().setTitle("Staff Dashboard");
        } else {
            getSupportActionBar().setTitle("Events");
        }

        // Setup Navigation
        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Show bottom navigation only for staff
        if (sharedPrefManager.isStaff()) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            setupStaffNavigation();
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            // If student, use the events fragment
            navController.getGraph().setStartDestination(R.id.navigation_events);
        }

        // Update title when destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (sharedPrefManager.isStaff()) {
                getSupportActionBar().setTitle(destination.getLabel());
            }
        });
    }

    private void setupStaffNavigation() {
        // Connect bottom navigation with navigation controller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sharedPrefManager.logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}