package edu.northeastern.pawpalsgroup5;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import edu.northeastern.pawpalsgroup5.models.User;

public class ProfileSetup extends AppCompatActivity {
    private EditText petNameEditText;
    private EditText breedEditText;
    private EditText ageEditText;
    private EditText introductionEditText;
    private Button btnSaveProfile;
    private Button upload;
    private ImageView profileImageView;
    private Uri selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        petNameEditText = findViewById(R.id.petNameEditText);
        breedEditText = findViewById(R.id.breedEditText);
        ageEditText = findViewById(R.id.ageEditText);
        introductionEditText = findViewById(R.id.introductionEditText);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        upload = findViewById(R.id.btnChangeProfilePic);
        profileImageView = findViewById(R.id.profileImageView);

        upload.setOnClickListener(view -> openGallery());

        btnSaveProfile.setOnClickListener(view -> {
            if (selectedImage != null) {
                String petName = petNameEditText.getText().toString().trim();
                String breed = breedEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();
                String description = introductionEditText.getText().toString().trim();

                if (petName.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
                    Toast.makeText(ProfileSetup.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("profileImages/" + UUID.randomUUID().toString());
                storageRef.putFile(selectedImage).addOnSuccessListener(taskSnapshot -> {
                    // Once the image is uploaded, get its download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

                            // Create the user object with the image URL
                            User user = new User(age, breed, description, petName, imageUrl);

                            // Save the user information in Firebase Realtime Database
                            databaseRef.child(userId).setValue(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(ProfileSetup.this, "Profile information saved successfully.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileSetup.this, MainActivity.class));
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ProfileSetup.this, "Failed to save profile information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(ProfileSetup.this, "User authentication required.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Handle failed download URL retrieval
                        Toast.makeText(ProfileSetup.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    // Handle failed image upload
                    Toast.makeText(ProfileSetup.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(ProfileSetup.this, "Please select an image to upload.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileSetup.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    selectedImage = result.getData().getData();
                    profileImageView.setImageURI(selectedImage);
                }
            });
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}

