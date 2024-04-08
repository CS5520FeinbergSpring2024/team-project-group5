package edu.northeastern.pawpalsgroup5;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.pawpalsgroup5.models.Post;

public class ProfileFragment extends Fragment {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser != null ? currentUser.getUid() : null;
    private ImageView profileImageView;
    RecyclerView recyclerView;
    ProfilePostAdaptor adaptor;
    private List<Post> postList;


    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.postHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        adaptor = new ProfilePostAdaptor(getContext(), postList);
        recyclerView.setAdapter(adaptor);

        profileImageView = view.findViewById(R.id.profileImageView);
        fetchUserInfoAndPopulateUI(view);

        loadPosts();
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

    private void loadPosts() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts");
        if (currentUser == null) {
            Log.e("FeedFragment", "User not found");
            return;
        }
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear the old data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null && post.getUserId().equals(currentUserId)) {
                        post.setPostId(postSnapshot.getKey().toString());  // Setting the postId
                        postList.add(post);
                    }
                }
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("FeedFragment", "Error loading posts", databaseError.toException());
            }
        });
    }
}