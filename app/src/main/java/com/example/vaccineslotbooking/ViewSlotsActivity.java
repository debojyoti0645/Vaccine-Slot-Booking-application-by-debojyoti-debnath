package com.example.vaccineslotbooking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewSlotsActivity extends AppCompatActivity {

    private ListView listViewSlots;
    private ArrayAdapter<Slot> adapter;
    private List<Slot> slotList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slots);

        db = FirebaseFirestore.getInstance();
        listViewSlots = findViewById(R.id.listViewSlots);
        slotList = new ArrayList<>();

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<Slot>(this, R.layout.slot_item, slotList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.slot_item, parent, false);
                }

                // Find views
                TextView tvSlotInfo = convertView.findViewById(R.id.tvSlotInfo);
                TextView tvStatusAvailable = convertView.findViewById(R.id.tvStatusAvailable);
                TextView tvStatusCancelled = convertView.findViewById(R.id.tvStatusCancelled);

                // Get the slot data
                Slot slot = slotList.get(position);

                // Store fetched data into variables
                String date = slot.getDate();
                String time = slot.getTime();
                String location = slot.getLocation();
                String vaccineName = slot.getVaccineName();
                int dosageNumber = slot.getDosageNumber();
                String status = slot.getStatus();

                // Construct the display text for slot details
                String displayText = "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Location: " + location + "\n" +
                        "Vaccine: " + vaccineName + "\n" +
                        "Dosage: " + dosageNumber;

                // Set slot information
                tvSlotInfo.setText(displayText);

                // Show status based on the slot status
                if (status.equals("Available")) {
                    tvStatusAvailable.setVisibility(View.VISIBLE);
                    tvStatusCancelled.setVisibility(View.GONE);
                } else if (status.equals("Cancelled")) {
                    tvStatusAvailable.setVisibility(View.GONE);
                    tvStatusCancelled.setVisibility(View.VISIBLE);
                }

                // Set click listener for each slot item
                convertView.setOnClickListener(v -> showEditSlotDialog(slot));

                return convertView;
            }
        };

        listViewSlots.setAdapter(adapter);

        // Fetch slots from Firestore
        listenForSlotChanges();
    }

    private void listenForSlotChanges() {
        db.collection("slots")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ViewSlotsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            slotList.clear();  // Clear the list to avoid duplicates
                            for (QueryDocumentSnapshot document : value) {
                                // Map Firestore data to Slot model
                                Slot slot = document.toObject(Slot.class);
                                slot.setId(document.getId());  // Store document ID for later updates
                                slotList.add(slot);  // Add the slot to the list
                            }
                            adapter.notifyDataSetChanged();  // Notify adapter of data change
                        }
                    }
                });
    }

    private void showEditSlotDialog(Slot slot) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_slot, null);

        // Find fields in dialog layout
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etVaccineName = dialogView.findViewById(R.id.etVaccineName);
        EditText etDosageNumber = dialogView.findViewById(R.id.etDosageNumber);
        Switch switchStatus = dialogView.findViewById(R.id.switchStatus);

        // Populate dialog fields with existing slot data
        etDate.setText(slot.getDate());
        etTime.setText(slot.getTime());
        etLocation.setText(slot.getLocation());
        etVaccineName.setText(slot.getVaccineName());
        etDosageNumber.setText(String.valueOf(slot.getDosageNumber()));
        switchStatus.setChecked(slot.getStatus().equals("Available"));

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Slot")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get updated values from dialog
                        String updatedDate = etDate.getText().toString();
                        String updatedTime = etTime.getText().toString();
                        String updatedLocation = etLocation.getText().toString();
                        String updatedVaccineName = etVaccineName.getText().toString();
                        int updatedDosageNumber = Integer.parseInt(etDosageNumber.getText().toString());
                        String updatedStatus = switchStatus.isChecked() ? "Available" : "Cancelled";

                        // Update slot data in Firestore
                        DocumentReference slotRef = db.collection("slots").document(slot.getId());
                        slotRef.update(
                                "date", updatedDate,
                                "time", updatedTime,
                                "location", updatedLocation,
                                "vaccineName", updatedVaccineName,
                                "dosageNumber", updatedDosageNumber,
                                "status", updatedStatus
                        ).addOnSuccessListener(aVoid -> {
                            Toast.makeText(ViewSlotsActivity.this, "Slot updated successfully.", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(ViewSlotsActivity.this, "Failed to update slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }
}
