package com.example.vaccineslotbooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class UserPanelActivity extends AppCompatActivity {

    private Button btnViewSlots, btnVaccinesTaken, btnDownloadCertificate, btnProfilePage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_panel);// Initialize the buttons
        btnViewSlots = findViewById(R.id.btnViewSlots);
        btnVaccinesTaken = findViewById(R.id.btnVaccinesTaken);
        btnDownloadCertificate = findViewById(R.id.btnDownloadCertificate);
        btnProfilePage = findViewById(R.id.btnProfilePage);

        // Set click listeners for each button
        btnViewSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the slot booking screen
                Intent intent = new Intent(UserPanelActivity.this, ViewAndBookSlotsActivity.class);
                startActivity(intent);
            }
        });

        btnVaccinesTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the screen showing vaccines taken
                Intent intent = new Intent(UserPanelActivity.this, VaccinesTakenActivity.class);
                startActivity(intent);
            }
        });

        btnDownloadCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the download certificate screen
                Intent intent = new Intent(UserPanelActivity.this, DownloadCertificateActivity.class);
                startActivity(intent);
            }
        });

        btnProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profile page screen
                Intent intent = new Intent(UserPanelActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}