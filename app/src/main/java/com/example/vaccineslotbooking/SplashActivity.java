package com.example.vaccineslotbooking;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the progress bar
        progressBarLogin = findViewById(R.id.progressBarLogin);

        // Show the progress bar
        progressBarLogin.setVisibility(View.VISIBLE);

        // Add a small delay to simulate loading effect and improve user experience
        new Handler().postDelayed(this::checkInternetConnection, 1500);
    }

    // Method to check internet connection
    private void checkInternetConnection() {
        if (isNetworkConnected()) {
            handleUserLogin();
        } else {
            showErrorDialog();
        }
    }

    // Method to check if the network is connected
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Method to handle login status
    private void handleUserLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRole(currentUser.getUid());
        } else {
            navigateToLoginActivity();
        }
    }

    // Method to check the user's role from Firestore
    private void checkUserRole(String uid) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String role = document.getString("role");
                    if ("admin".equals(role)) {
                        // Redirect to Admin dashboard
                        startActivity(new Intent(SplashActivity.this, AdminPanelActivity.class));
                    } else {
                        // Redirect to User homepage
                        startActivity(new Intent(SplashActivity.this, UserPanelActivity.class));
                    }
                    finish(); // Close splash activity
                } else {
                    Log.e("SplashActivity", "User role not found");
                    Snackbar.make(findViewById(android.R.id.content),
                            "Error loading user data. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                    navigateToLoginActivity();
                }
            } else {
                Log.e("SplashActivity", "Failed to get user role", task.getException());
                Snackbar.make(findViewById(android.R.id.content),
                        "Error loading user data. Please try again.",
                        Snackbar.LENGTH_LONG).show();
                navigateToLoginActivity();
            }
        });
    }

    // Method to navigate to login activity
    private void navigateToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Method to show the error dialog when there is no internet
    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry", (dialogInterface, i) -> {
                    // Retry checking the internet connection without restarting the activity
                    checkInternetConnection();
                })
                .setNegativeButton("Close", (dialogInterface, i) -> finish())
                .setCancelable(false)
                .show();
    }
}
