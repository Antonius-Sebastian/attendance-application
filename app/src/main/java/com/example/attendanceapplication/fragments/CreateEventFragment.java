package com.example.attendanceapplication.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.example.attendanceapplication.LoadingDialog;
import com.example.attendanceapplication.R;
import com.example.attendanceapplication.api.ApiClient;
import com.example.attendanceapplication.api.ApiService;
import com.example.attendanceapplication.models.ClassResponse;
import com.example.attendanceapplication.models.CreateEventRequest;
import com.example.attendanceapplication.models.EventResponse;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventFragment extends Fragment {
    private TextInputEditText titleInput, descriptionInput, dateInput, startTimeInput, endTimeInput;
    private MaterialButton submitButton;
    private ApiService apiService;
    private List<String> selectedClasses = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private MaterialAutoCompleteTextView classSelector;
    private ChipGroup chipGroup;
    private List<ClassResponse> classes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);
        loadingDialog = new LoadingDialog(requireActivity());

        initializeViews(view);
        setupDateTimePickers();
        setupClassSelection();
        setupSubmitButton();
        return view;
    }

    private void initializeViews(View view) {
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        dateInput = view.findViewById(R.id.dateInput);
        startTimeInput = view.findViewById(R.id.startTimeInput);
        endTimeInput = view.findViewById(R.id.endTimeInput);
        submitButton = view.findViewById(R.id.submitButton);
        classSelector = view.findViewById(R.id.classSelector);
        chipGroup = view.findViewById(R.id.selectedClassesChipGroup);

        // Set click listeners for date and time inputs
        dateInput.setOnClickListener(v -> showDatePicker());
        startTimeInput.setOnClickListener(v -> showTimePicker(startTimeInput));
        endTimeInput.setOnClickListener(v -> showTimePicker(endTimeInput));
    }

    private void setupDateTimePickers() {
        dateInput.setInputType(InputType.TYPE_NULL);
        startTimeInput.setInputType(InputType.TYPE_NULL);
        endTimeInput.setInputType(InputType.TYPE_NULL);
    }

    private void setupClassSelection() {
        String token = "Bearer " + SharedPrefManager.getInstance(requireContext()).getToken();
        apiService.getClasses(token).enqueue(new Callback<List<ClassResponse>>() {
            @Override
            public void onResponse(Call<List<ClassResponse>> call, Response<List<ClassResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    classes = response.body();
                    String[] classNames = classes.stream()
                            .map(ClassResponse::getName)
                            .toArray(String[]::new);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            classNames
                    );
                    classSelector.setAdapter(adapter);
                    setupClassSelectorListener();
                }
            }

            @Override
            public void onFailure(Call<List<ClassResponse>> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void setupClassSelectorListener() {
        classSelector.setOnItemClickListener((parent, view, position, id) -> {
            String selectedClass = parent.getItemAtPosition(position).toString();
            if (!selectedClasses.contains(selectedClass)) {
                selectedClasses.add(selectedClass);
                addChip(selectedClass);
                classSelector.setText("", false);
            }
        });
    }

    private void addChip(String className) {
        Chip chip = new Chip(requireContext());
        chip.setText(className);
        chip.setCloseIconVisible(true);
        chip.setClickable(false);
        chip.setCheckable(false);

        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            selectedClasses.remove(className);
        });

        chipGroup.addView(chip);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createEvent();
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateInput.setText(sdf.format(new Date(selection)));
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker(TextInputEditText timeInput) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d",
                    timePicker.getHour(), timePicker.getMinute());
            timeInput.setText(time);
        });

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (titleInput.getText().toString().trim().isEmpty()) {
            titleInput.setError("Title is required");
            isValid = false;
        }

        if (dateInput.getText().toString().trim().isEmpty()) {
            dateInput.setError("Date is required");
            isValid = false;
        }

        if (startTimeInput.getText().toString().trim().isEmpty()) {
            startTimeInput.setError("Start time is required");
            isValid = false;
        }

        if (endTimeInput.getText().toString().trim().isEmpty()) {
            endTimeInput.setError("End time is required");
            isValid = false;
        }

        if (selectedClasses.isEmpty()) {
            Snackbar.make(requireView(), "Select at least one class", Snackbar.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void createEvent() {
        loadingDialog.show();

        CreateEventRequest request = new CreateEventRequest(
                titleInput.getText().toString(),
                descriptionInput.getText().toString(),
                dateInput.getText().toString(),
                startTimeInput.getText().toString(),
                endTimeInput.getText().toString(),
                selectedClasses
        );

        String token = "Bearer " + SharedPrefManager.getInstance(requireContext()).getToken();

        apiService.createEvent(token, request).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    showSuccess("Event created successfully");
                    clearForm();
                } else {
                    showError("Failed to create event");
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                loadingDialog.dismiss();
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showSuccess(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showError(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    private void clearForm() {
        titleInput.setText("");
        descriptionInput.setText("");
        dateInput.setText("");
        startTimeInput.setText("");
        endTimeInput.setText("");
        classSelector.setText("");
        chipGroup.removeAllViews();
        selectedClasses.clear();
    }
}