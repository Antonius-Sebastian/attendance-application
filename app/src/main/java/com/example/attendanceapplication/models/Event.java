package com.example.attendanceapplication.models;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Event {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("date")
    private String date;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("classes")
    private List<String> classes;

    @SerializedName("entry_status")
    private String entryStatus;

    @SerializedName("exit_status")
    private String exitStatus;

    public Event() {
    }

    public Event(int id, String title, String description, String date, String startTime, String endTime, List<String> classes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classes = classes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<String> getClasses() {
        return classes;
    }
    public String getEntryStatus() { return entryStatus; }
    public String getExitStatus() { return exitStatus; }

    public boolean canGenerateEntryQR() {
        return entryStatus.equals("absent");
    }

    public boolean canGenerateQR() {
        try {
            // Parse event date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = dateFormat.parse(date);

            // Get today's date without time
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Compare dates
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTime(eventDate);
            eventCal.set(Calendar.HOUR_OF_DAY, 0);
            eventCal.set(Calendar.MINUTE, 0);
            eventCal.set(Calendar.SECOND, 0);
            eventCal.set(Calendar.MILLISECOND, 0);

            return eventCal.equals(today);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getButtonText() {
        if (entryStatus.equals("present") && exitStatus.equals("present")) {
            return "Attended";
        } else if (!canGenerateQR()) {
            // Check if event is past or future
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date eventDate = dateFormat.parse(date);
                Date today = new Date();

                if (eventDate.before(today)) {
                    return "Event Ended";
                } else {
                    return "Not Yet Available";
                }
            } catch (ParseException e) {
                return "Not Available";
            }
        } else if (entryStatus.equals("absent")) {
            return "Generate Entry QR";
        } else if (exitStatus.equals("absent")) {
            return "Generate Exit QR";
        } else {
            return "Not Available";
        }
    }

    @Override
    public String toString() {
        return title + " (" + date + ")";
    }
}