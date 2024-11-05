package com.example.vaccineslotbooking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerName, registerAge, registerLocation, registerPhNumber, registerEmail, registerPassword, registerConfPassword, adminKey;
    private TextView errorMsg;
    private Button registerButton, loginButton;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerName = findViewById(R.id.registerName);
        registerAge = findViewById(R.id.registerAge);
        registerLocation = findViewById(R.id.registerLocation);
        registerPhNumber = findViewById(R.id.registerPhNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfPassword = findViewById(R.id.registerConfPassword);
        adminKey = findViewById(R.id.adminKey);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        errorMsg = findViewById(R.id.errorMsg);

        registerButton.setOnClickListener(v -> registerUser());
        loginButton.setOnClickListener(v -> navigateToLogin(v));
    }

    private void registerUser() {
        String name = registerName.getText().toString().trim();
        String age = registerAge.getText().toString().trim();
        String location = registerLocation.getText().toString().trim();
        String phNumber = registerPhNumber.getText().toString().trim();
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String adminKeyInput = adminKey.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty() || location.isEmpty() || phNumber.isEmpty()) {
            vibrateDevice();
            errorMsg.setText("Please fill in all fields");
            errorMsg.setVisibility(View.VISIBLE);
            return;
        }
        if (!password.equals(registerConfPassword.getText().toString().trim())) {
            vibrateDevice();
            errorMsg.setText("Passwords do not match");
            errorMsg.setVisibility(View.VISIBLE);
            return;
        }

        // Check if adminKey is provided
        boolean isAdmin = !adminKeyInput.isEmpty() && adminKeyInput.equals("ADMIN628");
        if (!adminKeyInput.isEmpty() && !isAdmin) {
            vibrateDevice();
            errorMsg.setText("Wrong Admin Key");
            errorMsg.setVisibility(View.VISIBLE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserDataToFirestore(user.getUid(), name, email, location, phNumber, isAdmin ? "admin" : "user");
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        vibrateDevice();
                        errorMsg.setVisibility(View.VISIBLE);
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            errorMsg.setText("Password should be at least 6 characters");
                        } catch (FirebaseAuthInvalidCredentialsException | IllegalArgumentException e) {
                            errorMsg.setText("Invalid email address");
                        } catch (FirebaseAuthUserCollisionException e) {
                            errorMsg.setText("Email already in use");
                        } catch (Exception e) {
                            errorMsg.setText("Registration failed: " + e.getMessage());
                        }
                    }
                });
    }


    private void saveUserDataToFirestore(String uid, String name, String email, String location, String phNumber, String adminKey) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("age", Integer.parseInt(registerAge.getText().toString()));
        userData.put("location", location);
        userData.put("phone_number", phNumber);
        userData.put("email", email);
        userData.put("doses_taken", 0);
        userData.put("vaccinated", false);
        userData.put("profile_picture_url", "");
        userData.put("gender", "");


        // Check the admin key to set the role
        if ("ADMIN628".equals(adminKey)) { // Example admin key
            userData.put("role", "admin");
        } else {
            userData.put("role", "user");
        }

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    // Redirect to login page or homepage
                    finish();
                })
                .addOnFailureListener(e -> Log.w("Error", "Error writing document", e));
    }

    // Method to navigate to login page for existing users
    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //vibrate device
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrate for 500 milliseconds
        }
    }
}