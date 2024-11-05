package com.example.vaccineslotbooking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddSlotActivity extends AppCompatActivity {

    private Button btnSelectDate, btnSelectTime, btnSubmitSlot, dosageBox;
    private Spinner spinnerLocation, spinnerVaccineName;
    private Calendar selectedDate;
    private String selectedTime;
    private int selectedDosage = 1; // Default dose value

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_slot);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSubmitSlot = findViewById(R.id.btnSubmitSlot);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerVaccineName = findViewById(R.id.spinnerVaccineName);
        dosageBox = findViewById(R.id.dosageBox);

        // Initialize date and time selection
        selectedDate = Calendar.getInstance();
        selectedTime = "";

        // Populate location spinner with sample locations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(adapter);

        // Populate name spinner with sample names
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.name_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVaccineName.setAdapter(adapter2);

        // Select Date button logic
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddSlotActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        btnSelectDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Select Time button logic
        btnSelectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddSlotActivity.this,
                    (view, hourOfDay, minute) -> {
                        selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                        btnSelectTime.setText(selectedTime);
                    }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Submit Slot button logic
        btnSubmitSlot.setOnClickListener(v -> {
            String location = spinnerLocation.getSelectedItem().toString();
            String vaccineName = spinnerVaccineName.getSelectedItem().toString();
            String dosageNumber = dosageBox.getText().toString().trim();

            // Validate inputs
            if (vaccineName.isEmpty() || dosageNumber.isEmpty() || selectedTime.isEmpty()) {
                vibrateDevice();
                Toast.makeText(AddSlotActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save slot to Firebase or local database
            saveSlotToFirebase(location, vaccineName, dosageNumber, selectedDate, selectedTime);
        });

        // Select Dosage Number button logic
        dosageBox.setOnClickListener(v -> {
            showDosagePickerDialog();
        });
    }
    // Method to show NumberPicker dialog for selecting dosage
    private void showDosagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);  // You can adjust the max value if needed
        numberPicker.setValue(selectedDosage); // Set previously selected value as default

        builder.setView(numberPicker);
        builder.setTitle("Select Dosage");

        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedDosage = numberPicker.getValue();
            dosageBox.setText(String.valueOf(selectedDosage));  // Display selected number on button
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void saveSlotToFirebase(String location, String vaccineName, String dosageNumber,
                                    Calendar date, String time) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to store slot details
        Map<String, Object> slot = new HashMap<>();
        slot.put("location", location);
        slot.put("vaccineName", vaccineName);
        slot.put("dosageNumber", Integer.parseInt(dosageNumber));

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date.getTime());
        slot.put("date", formattedDate);

        // Store time
        slot.put("time", time);

        // Initialize slot as not cancelled
        slot.put("status", "Available");

        // Add slot to Firestore (in the "slots" collection)
        db.collection("slots")
                .add(slot)
                .addOnSuccessListener(documentReference -> {
                    // Slot added successfully
                    Toast.makeText(AddSlotActivity.this, "Slot added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close AddSlotActivity after successful submission
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(AddSlotActivity.this, "Error adding slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    //vibrate device
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrate for 500 milliseconds
        }
    }
}