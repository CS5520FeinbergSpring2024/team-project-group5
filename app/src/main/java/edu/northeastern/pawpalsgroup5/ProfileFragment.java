package edu.northeastern.pawpalsgroup5;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView profileImageView;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = view.findViewById(R.id.profileImageView);
        fetchUserInfoAndPopulateUI(view);
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

                        ((TextView) view.findViewById(R.id.usernameTextView)).setText(petName);
                        ((TextView) view.findViewById(R.id.BreedTextView)).setText(breed);
                        ((TextView) view.findViewById(R.id.AgeTextView)).setText(age);
                        ((TextView) view.findViewById(R.id.introTextView)).setText(description);
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
}