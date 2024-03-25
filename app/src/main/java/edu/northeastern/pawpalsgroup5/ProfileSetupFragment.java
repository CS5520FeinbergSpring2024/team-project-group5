package edu.northeastern.pawpalsgroup5;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileSetupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileSetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileSetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileSetupFragment newInstance(String param1, String param2) {
        ProfileSetupFragment fragment = new ProfileSetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_setup, container, false);
        fetchUserInfoAndPopulateUI(view);

        EditText petNameEditText = view.findViewById(R.id.petNameEditText);
        EditText breedEditText = view.findViewById(R.id.breedEditText);
        EditText ageEditText = view.findViewById(R.id.ageEditText);
        EditText introductionEditText = view.findViewById(R.id.introductionEditText);
        Button btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        btnSaveProfile.setOnClickListener(v -> {
            String petName = petNameEditText.getText().toString().trim();
            String breed = breedEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();
            String description = introductionEditText.getText().toString().trim();

            if (petName.isEmpty() || breed.isEmpty() || age.isEmpty() || description.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                HashMap<String, Object> userData = new HashMap<>();
                userData.put("petName", petName);
                userData.put("breed", breed);
                userData.put("age", age);
                userData.put("description", description);

                databaseRef.updateChildren(userData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getActivity(), "Profile information saved successfully.", Toast.LENGTH_SHORT).show();
                            // Handle success, e.g., navigate to another fragment or activity
                        })
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to save profile information.", Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(getActivity(), "User authentication required.", Toast.LENGTH_SHORT).show();
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
                        String age = dataSnapshot.child("age").getValue(String.class); // Firebase stores integers as Long
                        String description = dataSnapshot.child("description").getValue(String.class);

                        // Update the EditText fields
                        ((EditText) view.findViewById(R.id.petNameEditText)).setText(petName);
                        ((EditText) view.findViewById(R.id.breedEditText)).setText(breed);
                        if (age != null) {
                            ((EditText) view.findViewById(R.id.ageEditText)).setText(age.toString());
                        }
                        ((EditText) view.findViewById(R.id.introductionEditText)).setText(description);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("DatabaseError", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
    }


}