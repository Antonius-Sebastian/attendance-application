package com.example.attendanceapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.attendanceapplication.activities.LoginActivity;
import com.example.attendanceapplication.models.User;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "attendance_pref";

    // Keys for storing data
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_CLASS_ID = "user_class_id";
    private static final String KEY_USER_CLASS_NAME = "user_class_name";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    // Private constructor for singleton pattern
    private SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    // Singleton getInstance method
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // Save user data after successful login
    public void saveUser(User user, String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, token);

        // Save class information if available
        if (user.getClassId() != null) {
            editor.putInt(KEY_USER_CLASS_ID, user.getClassId());
        }
        if (user.getClassName() != null) {
            editor.putString(KEY_USER_CLASS_NAME, user.getClassName());
        }

        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    // Get stored token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Get stored user data
    public User getUser() {
        if (!isLoggedIn()) {
            return null;
        }

        return new User(
                sharedPreferences.getInt(KEY_USER_ID, -1),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_ROLE, null),
                sharedPreferences.contains(KEY_USER_CLASS_ID) ?
                        sharedPreferences.getInt(KEY_USER_CLASS_ID, -1) : null,
                sharedPreferences.getString(KEY_USER_CLASS_NAME, null)
        );
    }

    // Get user role
    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, null);
    }

    public Integer getUserClassId() {
        return sharedPreferences.contains(KEY_USER_CLASS_ID) ?
                sharedPreferences.getInt(KEY_USER_CLASS_ID, -1) : null;
    }

    public String getUserClassName() {
        return sharedPreferences.getString(KEY_USER_CLASS_NAME, null);
    }

    // Check if user is staff
    public boolean isStaff() {
        String role = getUserRole();
        return role != null && role.equals("staff");
    }

    // Clear stored data on logout
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login activity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
