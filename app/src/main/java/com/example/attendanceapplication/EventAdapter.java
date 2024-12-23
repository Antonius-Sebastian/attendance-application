package com.example.attendanceapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapplication.activities.QRScannerActivity;
import com.example.attendanceapplication.models.Event;
import com.example.attendanceapplication.models.User;
import com.example.attendanceapplication.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import java.util.List;

// EventAdapter.java
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private Context context;
    private boolean isStaff;

    public EventAdapter(Context context, List<Event> events, boolean isStaff) {
        this.context = context;
        this.events = events;
        this.isStaff = isStaff;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        // Set event details
        holder.titleView.setText(event.getTitle());
        holder.dateTimeView.setText(String.format("%s, %s - %s WIB",
                event.getDate(), event.getStartTime(), event.getEndTime()));
        holder.descriptionView.setText(event.getDescription());

        // Setup chips for classes
        setupChips(holder.chipGroup, event.getClasses());

        // Setup scan button
        MaterialButton scanButton = holder.scanButton;

        if (isStaff) {
            if (event.canGenerateQR()) {
                scanButton.setText("Scan QR");
                scanButton.setEnabled(true);
                scanButton.setOnClickListener(v -> openScanner(event));
            } else {
                scanButton.setText("Not Available");
                scanButton.setEnabled(false);
                scanButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.md_theme_outline)));
            }
        } else {
            scanButton.setText(event.getButtonText());
            boolean canGenerateQR = event.canGenerateQR() &&
                    (event.getEntryStatus().equals("absent") ||
                            (event.getEntryStatus().equals("present") && event.getExitStatus().equals("absent")));

            scanButton.setEnabled(canGenerateQR);

            if (canGenerateQR) {
                scanButton.setOnClickListener(v -> showQRDialog(event));
            }

            // You can also change button appearance based on state
            if (!event.canGenerateQR()) {
                scanButton.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.md_theme_outline)));
            }
        }
    }

    private void setupChips(ChipGroup chipGroup, List<String> classes) {
        chipGroup.removeAllViews();
        for (String className : classes) {
            Chip chip = new Chip(context);
            chip.setText(className);
            chip.setCheckable(false);
            chip.setEnabled(false);
            chipGroup.addView(chip);
        }
    }

    private void showQRDialog(Event event) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_qr_code, null);

        ImageView qrImageView = dialogView.findViewById(R.id.imageViewQR);
        TextView statusText = dialogView.findViewById(R.id.statusText);

        // Get QR type based on attendance status
        String qrType = event.canGenerateEntryQR() ? "entry" : "exit";

        // Generate QR code
        generateQRCode(qrImageView, event, qrType);

        // Set status text
        statusText.setText(qrType.equals("entry") ? "Entry QR Code" : "Exit QR Code");

        builder.setView(dialogView)
                .setTitle("Your QR Code")
                .setPositiveButton("Close", null)
                .show();
    }

    private void generateQRCode(ImageView imageView, Event event, String type) {
        try {
            User currentUser = SharedPrefManager.getInstance(context).getUser();

            // Create QR data
            JSONObject qrData = new JSONObject();
            qrData.put("eventId", event.getId());
            qrData.put("userId", currentUser.getId());
            qrData.put("type", type);

            // Generate QR code
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(qrData.toString(),
                    BarcodeFormat.QR_CODE, 512, 512);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Convert to bitmap
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            Toast.makeText(context, "Error generating QR code",
                    Toast.LENGTH_SHORT).show();
            Log.d("QR", "Error generating QR code:" + e.getMessage());
        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView dateTimeView;
        TextView descriptionView;
        ChipGroup chipGroup;
        MaterialButton scanButton;

        EventViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
            dateTimeView = itemView.findViewById(R.id.date_time);
            descriptionView = itemView.findViewById(R.id.description);
            chipGroup = itemView.findViewById(R.id.chip_group);
            scanButton = itemView.findViewById(R.id.scan_button);
        }
    }

    private void openScanner(Event event) {
        Intent intent = new Intent(context, QRScannerActivity.class);
        intent.putExtra("eventId", event.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}