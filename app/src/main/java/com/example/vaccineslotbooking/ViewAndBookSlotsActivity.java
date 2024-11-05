package com.example.vaccineslotbooking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ViewAndBookSlotsActivity extends AppCompatActivity {
    private ListView availableSlots;
    private FirebaseFirestore db;
    private UserSlotAdapter userSlotAdapter;
    private List<UserSlot> userSlots;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_book_slots);

        availableSlots = findViewById(R.id.availableSlots);
        db = FirebaseFirestore.getInstance();
        userSlots = new ArrayList<>();
        userSlotAdapter = new UserSlotAdapter(this, R.layout.list_item_slot, userSlots);
        availableSlots.setAdapter(userSlotAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e("ViewAndBookSlotsActivity", "User is not authenticated, userId is null");
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        loadUserSlots();
    }

    private void loadUserSlots() {
        db.collection("slots")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userSlots.clear();  // Clear the list before reloading
                        QuerySnapshot snapshots = task.getResult();
                        if (snapshots != null) {
                            for (DocumentSnapshot doc : snapshots) {
                                String status = doc.getString("status"); // Check the slot's status

                                // Skip the slot if it is canceled
                                if ("Cancelled".equalsIgnoreCase(status)) {
                                    continue;
                                }

                                String slotId = doc.getId();
                                String date = doc.getString("date");
                                String location = doc.getString("location");
                                String vaccine = doc.getString("vaccineName");
                                String time = doc.getString("time");
                                Long availability = doc.getLong("dosageNumber");

                                if (slotId != null) {
                                    userSlots.add(new UserSlot(date, location, vaccine, time, availability, slotId));
                                } else {
                                    Log.e("ViewAndBookSlotsActivity", "Slot ID is null for slot: " + vaccine);
                                }
                            }
                            userSlotAdapter.notifyDataSetChanged();  // Notify the adapter to update the ListView
                        }
                    } else {
                        Toast.makeText(this, "Error fetching slots", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void showConfirmationDialog(UserSlot slot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Booking")
                .setMessage("Do you want to book this slot?\n" +
                        "Date: " + slot.getSlotDate() + "\n" +
                        "Location: " + slot.getSlotLocation() + "\n" +
                        "Vaccine: " + slot.getVaccineName() + "\n" +
                        "Time: " + slot.getSlotTime())
                .setPositiveButton("Confirm", (dialog, which) -> bookSlot(slot))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void bookSlot(UserSlot slot) {
        String slotId = slot.getId();
        if (slotId == null || userId == null) {
            Toast.makeText(this, "Error booking slot: Missing slot or user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference userRef = db.collection("users").document(userId);
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            Long dosesTaken = userSnapshot.getLong("doses_taken");

            if (dosesTaken == null) {
                dosesTaken = 0L;
            }

            DocumentReference slotRef = db.collection("slots").document(slotId);
            DocumentSnapshot slotSnapshot = transaction.get(slotRef);
            Long availableDoses = slotSnapshot.getLong("dosageNumber");

            if (availableDoses == null || availableDoses <= 0) {
                throw new FirebaseFirestoreException("No more doses available", FirebaseFirestoreException.Code.ABORTED);
            }

            transaction.update(userRef, "doses_taken", dosesTaken + 1);
            DocumentReference bookingRef = userRef.collection("bookedVaccines").document();
            transaction.set(bookingRef, slot.toMap());
            transaction.update(slotRef, "dosageNumber", availableDoses - 1);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(ViewAndBookSlotsActivity.this, "Slot booked successfully!", Toast.LENGTH_SHORT).show();
            loadUserSlots();  // Reload slots to update the availability immediately after booking
        }).addOnFailureListener(e -> {
            Toast.makeText(ViewAndBookSlotsActivity.this, "Error booking slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
