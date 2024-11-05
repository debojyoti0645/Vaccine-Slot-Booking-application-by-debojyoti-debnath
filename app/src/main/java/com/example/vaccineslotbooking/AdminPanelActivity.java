package com.example.vaccineslotbooking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPanelActivity extends AppCompatActivity {

    private Button adminAddSlotButton, adminViewSlotButton, adminViewUserButton, adminLogoutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);

        // Initialize buttons
        adminAddSlotButton = findViewById(R.id.adminAddSlotButton);
        adminViewSlotButton = findViewById(R.id.adminViewSlotButton);
        adminViewUserButton = findViewById(R.id.adminViewUserButton);
        adminLogoutButton = findViewById(R.id.adminLogoutButton);

        // Add slot button click listener
        adminAddSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, AddSlotActivity.class);
                startActivity(intent);
            }
        });
        // View slots button click listener
        adminViewSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, ViewSlotsActivity.class);
                startActivity(intent);
            }
        });
        // Check user data button click listener
        adminViewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanelActivity.this, CheckUserDataActivity.class);
                startActivity(intent);
            }
        });
        // Logout button click listener
        adminLogoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Redirect to LoginActivity
            Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the backstack
            startActivity(intent);
            finish(); // Close current activity
        });
    }
}