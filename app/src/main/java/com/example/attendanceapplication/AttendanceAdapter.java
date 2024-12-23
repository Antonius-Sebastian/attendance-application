package com.example.attendanceapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapplication.models.AttendanceRecord;
import com.google.android.material.chip.Chip;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    private List<AttendanceRecord> attendanceList;
    private Context context;

    public AttendanceAdapter(Context context, List<AttendanceRecord> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord record = attendanceList.get(position);

        // Set student info
        holder.studentName.setText(record.getStudentName());
        holder.className.setText(record.getClassName());

        // Set entry status
        setupStatusChip(holder.entryStatus, "Entry", record.getEntryStatus());

        // Set exit status
        setupStatusChip(holder.exitStatus, "Exit", record.getExitStatus());
    }

    private void setupStatusChip(Chip chip, String type, String status) {
        chip.setText(type + ": " + status);

        if ("present".equals(status)) {
            chip.setChipBackgroundColorResource(R.color.chip_present);
            chip.setTextColor(context.getColor(android.R.color.white));
        } else {
            chip.setChipBackgroundColorResource(R.color.chip_absent);
            chip.setTextColor(context.getColor(android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public void updateData(List<AttendanceRecord> newAttendanceList) {
        this.attendanceList = newAttendanceList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        TextView className;
        Chip entryStatus;
        Chip exitStatus;

        ViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            className = itemView.findViewById(R.id.className);
            entryStatus = itemView.findViewById(R.id.entryStatus);
            exitStatus = itemView.findViewById(R.id.exitStatus);
        }
    }
}