package edu.northeastern.pawpalsgroup5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.squareup.picasso.Picasso;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import edu.northeastern.pawpalsgroup5.models.User;


public class ProfileSetupFragment extends Fragment {
    private EditText petNameEditText;
    private EditText breedEditText;
    private EditText ageEditText;
    private EditText introductionEditText;
    private Button btnSaveProfile;
    private Button upload;
    private ImageView profileImageView;
    private Uri selectedImage;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    public ProfileSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_setup, container, false);
        fetchUserInfoAndPopulateUI(view);

        petNameEditText = view.findViewById(R.id.petNameEditText);
        breedEditText = view.findViewById(R.id.breedEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        introductionEditText = view.findViewById(R.id.introductionEditText);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        upload = view.findViewById(R.id.btnChangeProfilePic);
        profileImageView = view.findViewById(R.id.profileImageView);

        upload.setOnClickListener(v -> openGallery());

        btnSaveProfile.setOnClickListener(v -> {
            String petName = petNameEditText.getText().toString().trim();
            String breed = breedEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();
            String description = introductionEditText.getText().toString().trim();
            if (petName.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedImage != null) {
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
                                        Toast.makeText(requireContext(), "Profile information saved successfully.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Failed to save profile information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(requireContext(), "User authentication required.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Handle failed download URL retrieval
                        Toast.makeText(requireContext(), "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    // Handle failed image upload
                    Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                String userId = currentUser.getUid();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
                DatabaseReference pictureRef = databaseRef.child(userId).child("picture");
                pictureRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Check if there is a valid picture for the user
                        if (dataSnapshot.exists()) {
                            String pictureUrl = dataSnapshot.getValue(String.class);
                            Log.d("pictureUrl", "pictureUrl: " + pictureUrl);
                            // Create the user object with the image URL
                            User user = new User(age, breed, description, petName, pictureUrl);
                            databaseRef.child(userId).setValue(user)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(requireContext(), "Profile information saved successfully.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Failed to save profile information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            System.out.println("Profile Picture needed");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Error fetching picture: " + databaseError.getMessage());
                    }
                });

            }
        });

        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return view;
    }

    public void fetchUserInfoAndPopulateUI(final View view) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String petName = dataSnapshot.child("petName").getValue(String.class);
                        String breed = dataSnapshot.child("breed").getValue(String.class);
                        String age = dataSnapshot.child("age").getValue(String.class);
                        String description = dataSnapshot.child("description").getValue(String.class);
                        String picture = dataSnapshot.child("picture").getValue(String.class);

                        // Update the EditText fields
                        ((EditText) view.findViewById(R.id.petNameEditText)).setText(petName);
                        ((EditText) view.findViewById(R.id.breedEditText)).setText(breed);
                        if (age != null) {
                            ((EditText) view.findViewById(R.id.ageEditText)).setText(age.toString());
                        }
                        ((EditText) view.findViewById(R.id.introductionEditText)).setText(description);
                        Picasso.get()
                                .load(picture)
                                .into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
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