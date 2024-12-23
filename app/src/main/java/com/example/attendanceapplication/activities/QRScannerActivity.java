package com.example.attendanceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceapplication.R;
import com.example.attendanceapplication.api.ApiClient;
import com.example.attendanceapplication.api.ApiService;
import com.example.attendanceapplication.models.AttendanceRequest;
import com.example.attendanceapplication.models.AttendanceResponse;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRScannerActivity extends AppCompatActivity {
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Verify staff role
        if (!sharedPrefManager.isStaff()) {
            finish();
            return;
        }

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Start scanner
        startScanner();
    }

    private void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);  // Use back camera
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Cancelled
                finish();
            } else {
                // Got content, process QR
                processQRContent(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processQRContent(String content) {
        try {
            JSONObject qrData = new JSONObject(content);
            int eventId = qrData.getInt("eventId");
            int userId = qrData.getInt("userId");
            String type = qrData.getString("type");

            recordAttendance(eventId, userId, type);
        } catch (JSONException e) {
            showError("Invalid QR Code format");
        }
    }

    private void recordAttendance(int eventId, int userId, String type) {
        String token = "Bearer " + sharedPrefManager.getToken();
        AttendanceRequest request = new AttendanceRequest(eventId, userId);

        Call<AttendanceResponse> call;
        if ("entry".equals(type)) {
            call = apiService.recordEntry(token, request);
        } else {
            call = apiService.recordExit(token, request);
        }

        call.enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showSuccess("Attendance recorded successfully" );
                } else {
                    try {
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        showError(errorBody.getString("message"));
                    } catch (Exception e) {
                        showError("Failed to record attendance"+ response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showSuccess(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("Scan Another", (dialog, which) -> {
                    startScanner();
                })
                .setNegativeButton("Done", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    startScanner();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}