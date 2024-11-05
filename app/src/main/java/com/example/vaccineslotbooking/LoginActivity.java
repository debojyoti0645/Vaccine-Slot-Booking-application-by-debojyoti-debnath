package com.example.vaccineslotbooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout main;
    private TextView loginEmail, loginPassword,registerMsg, errorMsg;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        main = findViewById(R.id.main);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        errorMsg = findViewById(R.id.errorMsg);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.resgisterButton);
        registerMsg = findViewById(R.id.registerMsg);

        // Hide keyboard when the background is touched
        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        // Set onClickListener for the login button
        loginButton.setOnClickListener(v -> loginUser());
        // Set onClickListener for the register text view
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            vibrateDevice();
            errorMsg.setText("Please enter your email");
            errorMsg.setVisibility(View.VISIBLE);
            return;
        }

        if (password.isEmpty()) {
            vibrateDevice();
            errorMsg.setText("Please enter your password");
            errorMsg.setVisibility(View.VISIBLE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        errorMsg.setText("Login failed. Please check your credentials.");
                        errorMsg.setVisibility(View.VISIBLE);
                        vibrateDevice();
                    }
                });
    }

    private void checkUserRole(String uid) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role = document.getString("role");
                    if ("admin".equals(role)) {
                        // Redirect to Admin dashboard
                        Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                        startActivity(intent);
                    } else {
                        // Redirect to User homepage
                        Intent intent = new Intent(LoginActivity.this, UserPanelActivity.class);
                        startActivity(intent);
                    }
                    finish();  // Close login activity
                } else {
                    Toast.makeText(LoginActivity.this, "User role not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Error", "Failed to get user role", task.getException());
            }
        });
    }

    // Hide keyboard method
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus(); // Remove focus from the EditText
        }
    }
    //vibrate device
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrate for 500 milliseconds
        }
    }
}