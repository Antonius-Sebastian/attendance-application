package com.example.attendanceapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapplication.models.AttendanceRecord;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupedAttendanceAdapter extends RecyclerView.Adapter<GroupedAttendanceAdapter.GroupViewHolder> {
    private Context context;
    private List<String> classNames;
    private Map<String, List<AttendanceRecord>> groupedAttendance;
    private Map<String, Boolean> expandedGroups;

    public GroupedAttendanceAdapter(Context context, List<AttendanceRecord> attendanceRecords) {
        this.context = context;
        this.groupedAttendance = new HashMap<>();
        this.expandedGroups = new HashMap<>();

        // Group attendance records by class
        for (AttendanceRecord record : attendanceRecords) {
            String className = record.getClassName();
            if (!groupedAttendance.containsKey(className)) {
                groupedAttendance.put(className, new ArrayList<>());
            }
            groupedAttendance.get(className).add(record);
        }

        // Create sorted list of class names
        this.classNames = new ArrayList<>(groupedAttendance.keySet());
        classNames.sort(String::compareTo);

        // Initialize all groups as collapsed
        for (String className : classNames) {
            expandedGroups.put(className, false);
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String className = classNames.get(position);
        List<AttendanceRecord> records = groupedAttendance.get(className);
        boolean isExpanded = expandedGroups.get(className);

        holder.className.setText(className);
        holder.studentCount.setText(records.size() + " students");

        // Set up the nested RecyclerView for students
        AttendanceAdapter studentAdapter = new AttendanceAdapter(context, records);
        holder.studentList.setLayoutManager(new LinearLayoutManager(context));
        holder.studentList.setAdapter(studentAdapter);

        // Show/hide student list based on expanded state
        holder.studentList.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setRotation(isExpanded ? 180 : 0);

        // Handle group expansion/collapse
        holder.itemView.setOnClickListener(v -> {
            boolean expanded = expandedGroups.get(className);
            expandedGroups.put(className, !expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return classNames.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView className;
        MaterialTextView studentCount;
        RecyclerView studentList;
        ImageView expandIcon;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            studentCount = itemView.findViewById(R.id.studentCount);
            studentList = itemView.findViewById(R.id.studentList);
            expandIcon = itemView.findViewById(R.id.expandIcon);
        }
    }
}