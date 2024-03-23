package edu.northeastern.pawpalsgroup5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
public class ProfileSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        EditText petNameEditText = findViewById(R.id.petNameEditText);
        EditText breedEditText = findViewById(R.id.breedEditText);
        EditText ageEditText = findViewById(R.id.ageEditText);
        EditText introductionEditText = findViewById(R.id.introductionEditText);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);

        btnSaveProfile.setOnClickListener(view -> {
            String petName = petNameEditText.getText().toString().trim();
            String breed = breedEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();
            String description = introductionEditText.getText().toString().trim();

            if (petName.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
                Toast.makeText(ProfileSetup.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

                HashMap<String, Object> userData = new HashMap<>();
                userData.put("petName", petName);
                userData.put("breed", breed);
                userData.put("age", age);
                userData.put("description", description);

                databaseRef.child(userId).setValue(userData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(ProfileSetup.this, "Profile information saved successfully.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ProfileSetup.this, MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileSetup.this, "Failed to save profile information.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ProfileSetup.this, "User authentication required.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}