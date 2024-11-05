package com.example.vaccineslotbooking;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CheckUserDataActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnCheckUser, buttonReset;
    private TextView statusHead, statusName, statusEmail, statusVaccinated, statusDosesTaken, statusGender, statusPhone;
    private ConstraintLayout constraintDetails;
    private ImageView profilePicture;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_data);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        btnCheckUser = findViewById(R.id.btnCheckUser);
        buttonReset = findViewById(R.id.buttonReset);
        statusHead = findViewById(R.id.statusHead);
        statusName = findViewById(R.id.statusName);
        statusEmail = findViewById(R.id.statusEmail);
        statusVaccinated = findViewById(R.id.statusVaccinated);
        statusDosesTaken = findViewById(R.id.statusDosesTaken);
        statusGender = findViewById(R.id.statusGender);
        statusPhone = findViewById(R.id.statusPhone);
        profilePicture = findViewById(R.id.profilePicture);
        constraintDetails = findViewById(R.id.constraintDetails);

        btnCheckUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the search when the button is clicked
                checkUser();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPage();
            }
        });
    }

    private void checkUser() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            // Vibrate the device for error
            vibrateDevice();
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore to find the document where 'email' field matches
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                            // Extract user data from Firestore
                            String name = document.getString("name");
                            String userEmail = document.getString("email");
                            Boolean isVaccinated = document.getBoolean("vaccinated");
                            Long dosesTaken = document.getLong("doses_taken");
                            String gender = document.getString("gender");
                            String phone = document.getString("phone_number");
                            String profilePicUrl = document.getString("profileImageUrl");

                            // Set default values if fields are missing
                            dosesTaken = dosesTaken == null ? 0L : dosesTaken;
                            isVaccinated = isVaccinated == null ? false : isVaccinated;

                            // Display user details in the UI
                            statusHead.setVisibility(View.VISIBLE);
                            statusName.setText("Name: " + name);
                            statusEmail.setText("Email: " + userEmail);
                            statusVaccinated.setText("Vaccinated: " + (isVaccinated ? "Yes" : "No"));
                            statusDosesTaken.setText("Doses Taken: " + dosesTaken );
                            statusGender.setText("Gender: " + gender);
                            statusPhone.setText("Phone: " + phone);

                            // Load profile picture if URL is available
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(this).load(profilePicUrl).into(profilePicture);
                                profilePicture.setVisibility(View.VISIBLE);
                            } else {
                                profilePicture.setVisibility(View.GONE);
                            }

                            // Make fields visible
                            constraintDetails.setVisibility(View.VISIBLE);
                            statusName.setVisibility(View.VISIBLE);
                            statusEmail.setVisibility(View.VISIBLE);
                            statusVaccinated.setVisibility(View.VISIBLE);
                            statusDosesTaken.setVisibility(View.VISIBLE);
                            statusGender.setVisibility(View.VISIBLE);
                            statusPhone.setVisibility(View.VISIBLE);
                            buttonReset.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(CheckUserDataActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            vibrateDevice();
                        }
                    } else {
                        Toast.makeText(CheckUserDataActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        vibrateDevice();
                    }
                });
    }

    private void resetPage() {
        // Clear all fields and hide views
        etEmail.setText("");
        constraintDetails.setVisibility(View.GONE);
        statusHead.setVisibility(View.GONE);
        statusName.setVisibility(View.GONE);
        statusEmail.setVisibility(View.GONE);
        statusVaccinated.setVisibility(View.GONE);
        statusDosesTaken.setVisibility(View.GONE);
        statusGender.setVisibility(View.GONE);
        statusPhone.setVisibility(View.GONE);
        profilePicture.setVisibility(View.GONE);
        buttonReset.setVisibility(View.GONE);
    }

    // Vibrate device on error
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrate for 500 milliseconds
        }
    }
}
