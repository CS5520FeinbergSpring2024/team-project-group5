package edu.northeastern.pawpalsgroup5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeastern.pawpalsgroup5.models.Post;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private List<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.feedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        feedAdapter = new FeedAdapter(postList);
        recyclerView.setAdapter(feedAdapter);

        loadPosts();

        return view;
    }

    private void loadPosts() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear the old data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(postSnapshot.getKey().toString());  // Setting the postId
                        Log.d("POSTID", "postid" + post.getPostId());
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("FeedFragment", "Error loading posts", databaseError.toException());
            }
        });
    }

}
