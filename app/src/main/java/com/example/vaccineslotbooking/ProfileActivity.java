package com.example.vaccineslotbooking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userGender, userAge, userVaccinated, userEmail, userPhone, userLocation, userDoses;
    private ImageView profilePicture;
    private ProgressBar uploadProgressBar;
    private Button btnEditProfile, btnLogout;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private Uri profileImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        loadUserData();
        setupEditProfile();
        setupProfilePictureClick();
    }

    private void initializeViews() {
        userName = findViewById(R.id.userName);
        userGender = findViewById(R.id.userGender);
        userAge = findViewById(R.id.userAge);
        userVaccinated = findViewById(R.id.userVaccinated);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userLocation = findViewById(R.id.userLocation);
        userDoses = findViewById(R.id.userDoses);
        profilePicture = findViewById(R.id.profilePicture);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        //add function to logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase
                FirebaseAuth.getInstance().signOut();

                // Redirect the user to the Login Activity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();  // Close the ProfileActivity
            }
        });

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profilePictures");
    }

    private void loadUserData() {
        if (currentUser != null) {
            DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    userName.setText("Name: " + documentSnapshot.getString("name"));
                    userGender.setText("Gender: " + documentSnapshot.getString("gender"));

                    Object ageObj = documentSnapshot.get("age");
                    if (ageObj instanceof Number) {
                        userAge.setText("Age: " + ((Number) ageObj).intValue());
                    } else {
                        userAge.setText("Age: N/A");
                    }

                    userVaccinated.setText("Vaccinated: " + documentSnapshot.getBoolean("vaccinated"));
                    userEmail.setText("Registered Email: " + currentUser.getEmail());
                    userPhone.setText("Registered Phone No.: " + documentSnapshot.getString("phone_number"));
                    userLocation.setText("Registered Location: " + documentSnapshot.getString("location"));
                    userDoses.setText("Number of vaccines taken: " + documentSnapshot.getLong("doses_taken"));

                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    if (profileImageUrl != null) {
                        Picasso.get().load(profileImageUrl).into(profilePicture);
                    }
                }
            });
        }
    }

    private void setupEditProfile() {
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        Dialog editProfileDialog = new Dialog(this);
        editProfileDialog.setContentView(R.layout.dialog_edit_profile);

        EditText etName = editProfileDialog.findViewById(R.id.editName);
        EditText etGender = editProfileDialog.findViewById(R.id.editGender);
        EditText etAge = editProfileDialog.findViewById(R.id.editAge);
        EditText etLocation = editProfileDialog.findViewById(R.id.editLocation);

        etName.setText(userName.getText().toString().replace("Name: ", ""));
        etGender.setText(userGender.getText().toString().replace("Gender: ", ""));
        etAge.setText(userAge.getText().toString().replace("Age: ", ""));
        etLocation.setText(userLocation.getText().toString().replace("Registered Location: ", ""));

        Button btnSave = editProfileDialog.findViewById(R.id.btnSaveProfile);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String gender = etGender.getText().toString();
            String age = etAge.getText().toString();
            String location = etLocation.getText().toString();

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", name);
            updatedData.put("gender", gender);
            updatedData.put("age", Integer.parseInt(age));
            updatedData.put("location", location);

            firestore.collection("users").document(currentUser.getUid())
                    .set(updatedData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        loadUserData();
                        editProfileDialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    );
        });
        editProfileDialog.show();
    }

    private void setupProfilePictureClick() {
        profilePicture.setOnClickListener(v -> {
            DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains("profileImageUrl") && documentSnapshot.getString("profileImageUrl") != null) {
                    showProfilePictureOptions();
                } else {
                    showAddProfilePictureDialog();
                }
            });
        });
    }

    private void showAddProfilePictureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Profile Picture")
                .setMessage("You haven't added a profile picture yet. Would you like to add one?")
                .setPositiveButton("Add Picture", (dialog, which) -> chooseProfilePicture())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showProfilePictureOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture Options")
                .setItems(new String[]{"Change Picture", "Remove Picture"}, (dialog, which) -> {
                    if (which == 0) {
                        chooseProfilePicture();
                    } else if (which == 1) {
                        removeProfilePicture();
                    }
                })
                .show();
    }

    private void chooseProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void removeProfilePicture() {
        firestore.collection("users").document(currentUser.getUid())
                .update("profileImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show();
                    profilePicture.setImageResource(R.drawable.baseline_person_add_24);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            uploadProfilePicture();
        }
    }

    private void uploadProfilePicture() {
        if (profileImageUri != null) {
            StorageReference fileReference = storageReference.child(currentUser.getUid() + ".jpg");
            uploadProgressBar.setVisibility(View.VISIBLE);

            fileReference.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        firestore.collection("users").document(currentUser.getUid())
                                .update("profileImageUrl", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    uploadProgressBar.setVisibility(View.GONE);
                                    Picasso.get().load(downloadUrl).fit().centerCrop().into(profilePicture);
                                    Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .addOnFailureListener(e -> {
                        uploadProgressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
